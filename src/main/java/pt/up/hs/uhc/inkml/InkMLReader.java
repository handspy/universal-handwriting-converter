package pt.up.hs.uhc.inkml;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import pt.up.hs.uhc.base.BaseReader;
import pt.up.hs.uhc.base.PageReader;
import pt.up.hs.uhc.exceptions.UnsupportedFormatException;
import pt.up.hs.uhc.models.Dot;
import pt.up.hs.uhc.models.DotType;
import pt.up.hs.uhc.models.Page;
import pt.up.hs.uhc.models.Stroke;
import org.w3._2003.inkml.AnnotationType;
import org.w3._2003.inkml.InkType;
import org.w3._2003.inkml.ObjectFactory;
import org.w3._2003.inkml.TraceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Reader for InkML files.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class InkMLReader extends BaseReader {

    @Override
    public Page read(InputStream is) throws Exception {

        // unmarshal to input stream
        InkType ink;
        try{
            // creating the JAXB context
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            // creating the unmarshaller object
            Unmarshaller unmarshalObj = jaxbContext.createUnmarshaller();
            // calling the unmarshal method
            JAXBElement<InkType> inkElement = unmarshalObj
                    .unmarshal(new StreamSource(is), InkType.class);

            ink = inkElement.getValue();
        } catch(Exception e) {
            throw new Exception("Writing InkML to output stream", e);
        }

        // convert to page
        Page page = new Page()
                .addMetadata("id", ink.getDocumentID());
        long startTime = 0L;
        for (Object obj: ink.getDefinitionsOrContextOrTrace()) {
            if (obj instanceof AnnotationType) {
                AnnotationType annotation = (AnnotationType) obj;
                if (annotation.getType().equalsIgnoreCase("width")) {
                    page.setWidth(Double.parseDouble(annotation.getValue()));
                } else if (annotation.getType().equalsIgnoreCase("height")) {
                    page.setHeight(Double.parseDouble(annotation.getValue()));
                } else {
                    page.addMetadata(annotation.getType(), annotation.getValue());
                }
            } else if (obj instanceof TraceType) {
                TraceType trace = (TraceType) obj;
                if (page.getStrokes().isEmpty()) {
                    startTime = trace.getTimeOffset().longValue();
                }
                page.addStroke(readStroke(startTime, (TraceType) obj));
            }
        }

        return page;
    }

    @Override
    public List<Page> readArchive(ZipInputStream zis) {
        throw new UnsupportedFormatException();
    }

    @Override
    public List<Page> readArchive(TarArchiveInputStream tais) {
        throw new UnsupportedFormatException();
    }

    private Stroke readStroke(long captureStartTime, TraceType trace) {

        Stroke stroke = new Stroke()
                .startTime(
                        captureStartTime +
                                (trace.getTimeOffset() != null ? trace.getTimeOffset().longValue() : 0)
                )
                .endTime(
                        captureStartTime +
                                (trace.getTimeOffset() != null ? trace.getTimeOffset().longValue() : 0) +
                                (trace.getDuration() != null ? trace.getDuration().longValue() : 0)
                );

        String type = trace.getType();
        DotType dotType;
        if (type.equalsIgnoreCase("penDown")) {
            dotType = DotType.DOWN;
        } else if (type.equalsIgnoreCase("penUp")) {
            dotType = DotType.UP;
        } else {
            dotType = DotType.MOVE;
        }

        String[] dots = trace.getValue().split(",");
        for (String dotStr: dots) {
            String[] values = dotStr.split("\\s+");
            if (values.length < 3) {
                continue;
            }
            stroke.addDot(
                    new Dot(
                            Double.parseDouble(values[0]),
                            Double.parseDouble(values[1]),
                            Long.parseLong(values[2]),
                            dotType,
                            null
                    )
            );
        }

        return stroke;
    }
}
