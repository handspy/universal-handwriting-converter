package pt.up.hs.uhc.lspdf;

import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import pt.up.hs.uhc.base.MultiPageReader;
import pt.up.hs.uhc.exceptions.UnknownFormatException;
import pt.up.hs.uhc.models.Page;
import pt.up.hs.uhc.utils.StreamUtils;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;

public class LsPDFReader implements MultiPageReader {
    private static final String LIVESCRIBE_METADATA_KEY = "Livescribe_Metadata";
    private static final String LIVESCRIBE_INKML_KEY = "Livescribe_InkML";

    private static final Logger logger = Logger.getLogger(LsPDFReader.class.getName());

    @Override
    public List<Page> read(File file) throws Exception {
        return read(new FileInputStream(file));
    }

    public List<Page> read(InputStream is) throws Exception {
        return new LsInkMLReader().read(extractInkML(is));
    }

    /**
     * Extract the InkML from the PDF input stream.
     *
     * @param is {@link InputStream} PDF input stream
     * @return {@link InputStream} the InkML input stream.
     * @throws IOException if an error occurs while extracting InkML.
     */
    private static InputStream extractInkML(InputStream is) throws IOException {

        PDDocument pd = PDDocument.load(is);

        PDDocumentCatalog catalog = pd.getDocumentCatalog();

        COSObject lsObject = catalog.getCOSObject()
                .getCOSObject(COSName.getPDFName(LIVESCRIBE_METADATA_KEY));
        if (lsObject == null) {
            throw new UnknownFormatException("This player can only load" +
                    " PDFs created by Livescribe. Please select a Lives" +
                    "cribe PDF.");
        }

        COSObject inkMLObject = (COSObject) lsObject
                .getItem(COSName.getPDFName(LIVESCRIBE_INKML_KEY));
        if (inkMLObject != null) {
            COSStream inkMlCosStream = (COSStream) inkMLObject.getObject();
            ByteArrayInputStream bais = decompress(inkMlCosStream.createRawInputStream());
            pd.close();
            return bais;
        }

        return null;
    }

    /**
     * Decompress a zip-compressed stream.
     *
     * @param compressed {@link InputStream} compressed input stream
     * @return {@link ByteArrayInputStream} decompressed input stream
     * @throws IOException if an exception
     */
    private static ByteArrayInputStream decompress(InputStream compressed)
            throws IOException {
        InputStream in = new InflaterInputStream(
                new ByteArrayInputStream(StreamUtils.readAllBytes(compressed))
        );
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        shovelInToOut(in, buffer);
        in.close();
        buffer.close();
        compressed.close();
        return new ByteArrayInputStream(buffer.toByteArray());
    }

    /**
     * Shovels all data from an input stream to an output stream.
     *
     * @param in {@link InputStream} input stream
     * @param out {@link OutputStream} output stream
     */
    private static void shovelInToOut(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1000];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }
}
