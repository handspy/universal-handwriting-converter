package pt.up.hs.uhc.neonotes;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import pt.up.hs.uhc.base.BaseArchiveReader;
import pt.up.hs.uhc.base.PageReader;
import pt.up.hs.uhc.models.CaptureError;
import pt.up.hs.uhc.models.Dot;
import pt.up.hs.uhc.models.Page;
import pt.up.hs.uhc.models.Stroke;
import pt.up.hs.uhc.utils.PageUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Reader of Neo Notes' files.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NeoNotesReader extends BaseArchiveReader implements PageReader {

    // basic sizes
    private static final int BYTE_SIZE = 1;
    private static final int NUMBER_SIZE = 4; // integer or float
    private static final int LONG_SIZE = 8;

    // block sizes
    private static final int ID_SIZE = 3;
    private static final int HEADER_SIZE = ID_SIZE + 6 * NUMBER_SIZE + 2 * LONG_SIZE + BYTE_SIZE;
    private static final int STROKE_HEADER_SIZE = BYTE_SIZE + 2 * NUMBER_SIZE + LONG_SIZE;
    private static final int DOT_SIZE = 3 * NUMBER_SIZE + BYTE_SIZE;

    // reader props
    private static final int BUFFER_SIZE = 4096;

    @Override
    public Page readSingle(File file) throws Exception {
        return readSingle(new FileInputStream(file));
    }

    @Override
    public Page readSingle(InputStream inputStream) throws IOException {

        Page page = new Page();

        // see https://github.com/NeoSmartpen/Documentations/blob/master/NeoNote_data_Eng_V1.0.pdf
        try (BufferedInputStream bufferedStream = new BufferedInputStream(inputStream)) {
            ByteBuffer headerBuffer = getByteBuffer(bufferedStream, HEADER_SIZE);

            // read header metadata
            String id = getString(headerBuffer, ID_SIZE);
            int version = headerBuffer.getInt();
            int noteType = headerBuffer.getInt();
            int pageNo = headerBuffer.getInt();
            double w = headerBuffer.getFloat();
            double h = headerBuffer.getFloat();
            NCodePaperSize size = NCodePaperSize.getPaperSizeFor(
                    noteType,
                    w * Constants.NCODE_COORDINATES_TO_MM_FACTOR,
                    h * Constants.NCODE_COORDINATES_TO_MM_FACTOR
            );

            page
                    .addMetadata("id", id)
                    .addMetadata("version", version)
                    .addMetadata("noteType", noteType)
                    .addMetadata("pageNo", pageNo)
                    .width(size.getWidth())
                    .height(size.getHeight())
                    .marginLeft(size.getMarginLeft())
                    .marginTop(size.getMarginTop())
                    .marginRight(size.getMarginRight())
                    .marginBottom(size.getMarginBottom())
                    .addMetadata("createdTime", headerBuffer.getLong())
                    .addMetadata("modifiedTime", headerBuffer.getLong())
                    .addMetadata("dirtyBit", headerBuffer.get() != 0);

            int nStrokes = headerBuffer.getInt();

            // read strokes
            for (int c = 0; c < nStrokes; c++) {
                int kind = getByteBuffer(bufferedStream, BYTE_SIZE).get();
                if (kind == 0) { // ignore voice memo
                    page.addStroke(readStroke(bufferedStream));
                }
            }

            // read GUID of page
            int guidStringSize = getByteBuffer(bufferedStream, NUMBER_SIZE).getInt();
            page.addMetadata("pageGuidString", getString(
                    getByteBuffer(bufferedStream, guidStringSize),
                    guidStringSize)
            );
        }

        if (PageUtils.hasOverlappingStrokes(page)) {
            page.addMetadataCaptureError(CaptureError.STROKE_OVERLAP);
        } else if (PageUtils.hasDotOutOfContentArea(page)) {
            if (PageUtils.hasOutOfBoundariesDot(page)) {
                page.addMetadataCaptureError(CaptureError.OUT_OF_BOUNDS);
            } else {
                page.addMetadataCaptureError(CaptureError.MARGIN_NOT_RESPECTED);
            }
        }

        return page;
    }

    @Override
    public List<Page> readArchive(ZipInputStream zis) throws Exception {
        List<Page> pages = new ArrayList<>();
        ZipEntry zipEntry;
        while ((zipEntry = zis.getNextEntry()) != null) {
            if (zipEntry.getName().endsWith(".data")) {
                byte[] bytes = new byte[BUFFER_SIZE];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int count;
                while ((count = zis.read(bytes)) > 0) {
                    baos.write(bytes, 0, count);
                }

                pages.add(readSingle(new ByteArrayInputStream(baos.toByteArray())));

                baos.close();
            }
            zis.closeEntry();
        }
        zis.close();
        return pages;
    }

    @Override
    public List<Page> readArchive(TarArchiveInputStream tais) throws Exception {
        List<Page> pages = new ArrayList<>();
        TarArchiveEntry tarEntry;
        while ((tarEntry = (TarArchiveEntry) tais.getNextEntry()) != null) {
            if (tarEntry.getName().endsWith(".data")) {
                byte[] bytes = new byte[BUFFER_SIZE];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int count;
                while ((count = tais.read(bytes)) > 0) {
                    baos.write(bytes, 0, count);
                }

                pages.add(readSingle(new ByteArrayInputStream(baos.toByteArray())));

                baos.close();
            }
        }
        tais.close();
        return pages;
    }

    /**
     * Read a stroke from the given {@link BufferedInputStream}.
     *
     * @param bufferedStream {@link BufferedInputStream} stream with strokes.
     * @return {@link Stroke} stroke read from input stream
     * @throws IOException if an exception occurs while reading data.
     */
    private Stroke readStroke(BufferedInputStream bufferedStream) throws IOException {

        Stroke stroke = new Stroke();

        // read header metadata
        ByteBuffer headerBuffer = getByteBuffer(bufferedStream, STROKE_HEADER_SIZE);

        stroke
                .addMetadata("color", headerBuffer.getInt())
                .addMetadata("thickness", (int) headerBuffer.get());

        int nDots = headerBuffer.getInt();

        stroke.setStartTime(headerBuffer.getLong());

        // read dots
        long time = stroke.getStartTime();
        for (int c = 0; c < nDots; c++) {
            ByteBuffer dotBuffer = getByteBuffer(bufferedStream, DOT_SIZE);

            double x = dotBuffer.getFloat();
            double y = dotBuffer.getFloat();

            Dot dot = new Dot()
                    .x(x * Constants.NCODE_COORDINATES_TO_MM_FACTOR)
                    .y(y * Constants.NCODE_COORDINATES_TO_MM_FACTOR)
                    .pressure((double) dotBuffer.getFloat());

            int timeDiff = 0xFF & dotBuffer.get();

            time += timeDiff;

            stroke.addDot(dot.timestamp(time));
        }

        stroke.endTime(time);

        int extraDataLength = 0xFF & getByteBuffer(bufferedStream, BYTE_SIZE).get();
        getByteBuffer(bufferedStream, extraDataLength);

        return stroke;
    }


    /**
     * Create from buffered input stream a byte buffer with requested size for
     * reading values.
     *
     * @param bufferedStream {@link BufferedInputStream} Input stream to read
     *                       from.
     * @param size           Size in bytes to read.
     * @return {@link ByteBuffer} buffer with {@code size} bytes read from
     * input stream.
     * @throws IOException if an error occurs while reading the input stream.
     */
    private ByteBuffer getByteBuffer(BufferedInputStream bufferedStream, int size) throws IOException {
        byte[] backBuffer = new byte[size];
        bufferedStream.read(backBuffer);
        ByteBuffer headerBuffer = ByteBuffer.wrap(backBuffer);
        headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return headerBuffer;
    }


    /**
     * Get from buffer a string with requested size
     *
     * @param buffer {@link ByteBuffer} buffer to get string from
     * @param size   {@code int} size of the string to get (in bytes)
     * @return {@link String} string from buffer with requested size
     */
    private String getString(ByteBuffer buffer, int size) {
        byte[] stringBytes = new byte[size];
        buffer.get(stringBytes);
        return new String(stringBytes);
    }
}
