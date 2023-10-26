package pt.up.hs.uhc.lspdf;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import pt.up.hs.uhc.base.MultiPageReader;
import pt.up.hs.uhc.models.*;
import pt.up.hs.uhc.utils.PageUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LsInkMLReader implements MultiPageReader {

    @Override
    public List<Page> read(File file) throws Exception {
        return read(Files.newInputStream(file.toPath()));
    }

    @Override
    public List<Page> read(InputStream is) throws Exception {
        return parse(is);
    }

    public static List<Page> parse(InputStream is) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(is);

        doc.getDocumentElement().normalize();

        Node inkSourceNode = doc.getElementsByTagName("inkSource").item(0);

        String id = inkSourceNode.getAttributes().getNamedItem("xml:id").getNodeValue();
        String manufacturer = inkSourceNode.getAttributes().getNamedItem("manufacturer").getNodeValue();
        String model = inkSourceNode.getAttributes().getNamedItem("model").getNodeValue();
        String serialNo = inkSourceNode.getAttributes().getNamedItem("serialNo").getNodeValue();

        Node inkSourceSampleRateNode = ((Element) inkSourceNode).getElementsByTagName("sampleRate").item(0);

        boolean uniform = Boolean.parseBoolean(((Element) inkSourceSampleRateNode).getAttribute("uniform"));
        int value = Integer.parseInt(((Element) inkSourceSampleRateNode).getAttribute("value"));
        double rate = 1000D / (double) value;

        Node inkSourceTraceFormatNode = ((Element) inkSourceNode).getElementsByTagName("traceFormat").item(0);
        NodeList inkSourceTraceFormatChannelNodes = ((Element) inkSourceTraceFormatNode)
                .getElementsByTagName("channel");

        List<Channel> channels = new ArrayList<>();
        for (int i = 0; i < inkSourceTraceFormatChannelNodes.getLength(); i++) {
            Node inkSourceTraceFormatChannelNode = inkSourceTraceFormatChannelNodes.item(i);

            Channel channel = new Channel();
            channel.type = inkSourceTraceFormatChannelNode.getAttributes().getNamedItem("name").getNodeValue();
            channel.unit = inkSourceTraceFormatChannelNode.getAttributes().getNamedItem("units").getNodeValue();

            channels.add(channel);
        }

        return extractPages(doc, id, serialNo, manufacturer, model, rate, channels);
    }

    private static List<Page> extractPages(
            Document doc,
            String penId, String penSerialNo, String penManufacturer, String penModel,
            double rate, List<Channel> channels
    ) {

        List<Page> pages = new ArrayList<>();

        NodeList traceGroupNodes = doc.getElementsByTagName("traceGroup");
        for (int i = 0; i < traceGroupNodes.getLength(); i++) {

            Element traceGroup = (Element) traceGroupNodes.item(i);

            pages.add(extractPage(
                    traceGroup,
                    penId, penSerialNo, penManufacturer, penModel,
                    rate, channels
            ));
        }

        return pages;
    }

    private static Page extractPage(
            Element traceGroup,
            String penId, String penSerialNo, String penManufacturer, String penModel,
            double rate, List<Channel> channels
    ) {
        Page page = new Page()
                .addMetadataId(traceGroup.getAttributes().getNamedItem("xml:id").getNodeValue())
                .addMetadataPenId(penId)
                .addMetadataPenSerialNo(penSerialNo)
                .addMetadataPenManufacturer(penManufacturer)
                .addMetadataPenModel(penModel);

        Element traceGroupActiveArea = (Element) traceGroup.getElementsByTagName("activeArea").item(0);
        if (traceGroupActiveArea != null) {
            NamedNodeMap traceGroupActiveAreaAttrs = traceGroupActiveArea.getAttributes();
            Point size = extractDimensionPair(traceGroupActiveAreaAttrs.getNamedItem("mediaSize").getNodeValue());
            Point[] cropBounds = extractCropBounds(traceGroupActiveAreaAttrs.getNamedItem("cropBounds").getNodeValue());
            int pageIndex = Integer.parseInt(traceGroupActiveAreaAttrs.getNamedItem("pageIndex").getNodeValue());
            LengthUnit unit = LengthUnit.from(traceGroupActiveAreaAttrs.getNamedItem("units").getNodeValue().toUpperCase());

            page
                    .addMetadataPageNo(pageIndex)
                    .width(size.x * unit.getMmRate())
                    .height(size.y * unit.getMmRate());
        }

        Map<String, Timestamp> timestamps = new HashMap<>();

        NodeList traceGroupTimeStampNodes = traceGroup.getElementsByTagName("timestamp");
        for (int j = 0; j < traceGroupTimeStampNodes.getLength(); j++) {
            Element traceGroupTimeStamp = (Element) traceGroupTimeStampNodes.item(j);

            String id = traceGroupTimeStamp.getAttribute("xml:id");

            Timestamp timestamp = new Timestamp();
            if (traceGroupTimeStamp.hasAttribute("time")) {
                timestamp.time = Long.parseLong(traceGroupTimeStamp.getAttribute("time"));
            }
            if (traceGroupTimeStamp.hasAttribute("timeOffset")) {
                timestamp.offset = Long.parseLong(traceGroupTimeStamp.getAttribute("timeOffset"));
            }
            if (traceGroupTimeStamp.hasAttribute("timestampRef")) {
                timestamp.ref = traceGroupTimeStamp.getAttribute("timestampRef");
            }

            timestamps.put(id, timestamp);
        }

        NodeList traceGroupTraceNodes = traceGroup.getElementsByTagName("trace");
        for (int j = 0; j < traceGroupTraceNodes.getLength(); j++) {
            Element traceGroupTrace = (Element) traceGroupTraceNodes.item(j);

            String contextRef = traceGroupTrace.getAttribute("contextRef");
            long timeOffset = Long.parseLong(traceGroupTrace.getAttribute("timeOffset"));
            timeOffset =
                    Long.parseLong(traceGroupTrace.getAttribute("timeOffset")) +
                    calculateBaseTime(timestamps, contextRef);

            Stroke stroke = new Stroke()
                    .startTime(timeOffset);

            double originX = 0D;
            double originY = 0D;

            long endTime = timeOffset;

            String traceGroupTracePointsStr = traceGroupTrace.getTextContent();
            String[] traceGroupPoints = traceGroupTracePointsStr.split(", ");
            for (int k = 0; k < traceGroupPoints.length; k++) {
                String[] traceGroupPointValues = traceGroupPoints[k].split(" ");

                long time = timeOffset + Math.round(rate * k);
                double x = 0D;
                double y = 0D;
                double pressure = -1D;

                for (int k2 = 0; k2 < traceGroupPointValues.length; k2++) {
                    Channel channel = channels.get(k2);
                    if (channel.type.equalsIgnoreCase("X")) {
                        x = Double.parseDouble(traceGroupPointValues[k2]) * LengthUnit.from(channel.unit).getMmRate();
                    } else if (channel.type.equalsIgnoreCase("Y")) {
                        y = Double.parseDouble(traceGroupPointValues[k2]) * LengthUnit.from(channel.unit).getMmRate();
                    } else if (channel.type.equalsIgnoreCase("T")) {
                        time = Math.round(Long.parseLong(traceGroupPointValues[k2]) * TimeUnit.from(channel.unit).getMsRate());
                    } else if (channel.type.equalsIgnoreCase("F")) {
                        pressure = Double.parseDouble(traceGroupPointValues[k2]);
                    }
                }

                originX += x;
                originY += y;

                Dot dot = new Dot()
                        .x(originX)
                        .y(originY)
                        .timestamp(time);
                if (pressure >= 0) {
                    dot.pressure(pressure);
                }

                stroke.addDot(dot);

                endTime = time;
            }

            page.addStroke(stroke.endTime(endTime));
        }

        if (PageUtils.hasOverlappingStrokes(page)) {
            page.addMetadataCaptureError(CaptureError.STROKE_OVERLAP);
        }

        return page;
    }

    private static long calculateBaseTime(Map<String, Timestamp> timestamps, String contextRef) {
        Timestamp timestamp = timestamps.get(contextRef.substring(1));
        long offset = 0;
        while (timestamp.time == null) {
            offset += timestamp.offset;
            timestamp = timestamps.get(timestamp.ref.substring(1));
        }
        return offset + timestamp.time;
    }

    private static Point[] extractCropBounds(String value) {
        String cropboundsString = value.replace("{", "");
        cropboundsString = cropboundsString.replace("}", "");

        String[] cropboundsArr = cropboundsString.split("\\), \\(");

        Point[] cropbounds = new Point[2];
        cropbounds[0] = extractDimensionPair(cropboundsArr[0]);
        cropbounds[1] = extractDimensionPair(cropboundsArr[1]);

        return cropbounds;
    };

    private static Point extractDimensionPair(String value) {
        String pairStr = value.replace("(", "");
        pairStr = pairStr.replace(")", "");

        String[] pairArr = pairStr.split(",");

        Point point = new Point();
        point.x = Integer.parseInt(pairArr[0].trim());
        point.y = Integer.parseInt(pairArr[1].trim());

        return point;
    }

    static class Channel {
        String type;
        String unit;
    }

    static class Timestamp {
        Long time;
        Long offset;
        String ref;
    }

    static class Point {
        int x;
        int y;
    }
}
