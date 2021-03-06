package pt.up.hs.uhc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.up.hs.uhc.models.Dot;
import pt.up.hs.uhc.models.Format;
import pt.up.hs.uhc.models.Page;
import pt.up.hs.uhc.models.Stroke;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Test Universal Handwriting Converter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class UniversalHandwritingConverterTest {

    /*@Test
    public void testReadNeoNotesWithAutoDetect() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("neonotes/single/page_full.data"));

        Page page = uhc.getPage();
        Assertions.assertEquals("neo", page.getMetadata("id"));
        Assertions.assertEquals(609, page.getMetadata("noteType"));
        Assertions.assertEquals(1576500142905L, page.getMetadata("createdTime"));
        Assertions.assertEquals(1576500328443L, page.getMetadata("modifiedTime"));
        Assertions.assertEquals(79, page.getMetadata("pageNo"));
        Assertions.assertEquals(5, page.getMetadata("version"));
        Assertions.assertEquals(210.225928D, page.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(271.935572D, page.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(643, page.getStrokes().size());

        Stroke firstStroke = page.getStrokes().get(0);
        Assertions.assertEquals(1576499042448L, firstStroke.getStartTime());
        Assertions.assertEquals(1576499043432L, firstStroke.getEndTime());
        Assertions.assertEquals(97, firstStroke.getDots().size());
        Assertions.assertEquals(1576499042448L, firstStroke.getDots().get(0).getTimestamp());
        Assertions.assertEquals(28.8984257D, firstStroke.getDots().get(0).getX(), TestUtils.EPSILON);
        Assertions.assertEquals(72.8031712D, firstStroke.getDots().get(0).getY(), TestUtils.EPSILON);

        Stroke lastStroke = page.getStrokes().get(page.getStrokes().size() - 1);
        Assertions.assertEquals(1576500049236L, lastStroke.getStartTime());
        Assertions.assertEquals(1576500049319L, lastStroke.getEndTime());
        Assertions.assertEquals(6, lastStroke.getDots().size());
        Assertions.assertEquals(1576500049236L, lastStroke.getDots().get(0).getTimestamp());
        Assertions.assertEquals(86.1737297D, lastStroke.getDots().get(0).getX(), TestUtils.EPSILON);
        Assertions.assertEquals(280.9002883D, lastStroke.getDots().get(0).getY(), TestUtils.EPSILON);
    }*/

    @Test
    public void testReadNeoNotesWithFormatSpecifiedNormalized() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .inputFormat(Format.NEONOTES)
                .file(TestUtils.openFileForResource("neonotes/single/page_full.data"))
                .normalize(true, 3);

        Page page = uhc.getPage();
        Assertions.assertEquals("neo", page.getMetadata("id"));
        Assertions.assertEquals(609, page.getMetadata("noteType"));
        Assertions.assertEquals(1576500142905L, page.getMetadata("createdTime"));
        Assertions.assertEquals(1576500328443L, page.getMetadata("modifiedTime"));
        Assertions.assertEquals(79, page.getMetadata("pageNo"));
        Assertions.assertEquals(5, page.getMetadata("version"));
        Assertions.assertEquals(236.000D, page.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(323.000D, page.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(643, page.getStrokes().size());

        Stroke firstStroke = page.getStrokes().get(0);
        Assertions.assertEquals(0L, firstStroke.getStartTime());
        Assertions.assertEquals(984L, firstStroke.getEndTime());
        Assertions.assertEquals(97, firstStroke.getDots().size());
        Assertions.assertEquals(0L, firstStroke.getDots().get(0).getTimestamp());
        Assertions.assertEquals(28.898D, firstStroke.getDots().get(0).getX(), TestUtils.EPSILON);
        Assertions.assertEquals(72.803D, firstStroke.getDots().get(0).getY(), TestUtils.EPSILON);

        Stroke lastStroke = page.getStrokes().get(page.getStrokes().size() - 1);
        Assertions.assertEquals(1006788L, lastStroke.getStartTime());
        Assertions.assertEquals(1006871L, lastStroke.getEndTime());
        Assertions.assertEquals(6, lastStroke.getDots().size());
        Assertions.assertEquals(1006788L, lastStroke.getDots().get(0).getTimestamp());
        Assertions.assertEquals(86.174D, lastStroke.getDots().get(0).getX(), TestUtils.EPSILON);
        Assertions.assertEquals(280.900D, lastStroke.getDots().get(0).getY(), TestUtils.EPSILON);
    }

    @Test
    public void testReadNeoNotesArchiveWithAutoDetect() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("neonotes/archive/archive.neonotes.zip"));

        Assertions.assertEquals(2, uhc.getPages().size());

        Page firstPage = uhc.getPages().get(0);
        Assertions.assertEquals("neo", firstPage.getMetadata("id"));
        Assertions.assertEquals(609, firstPage.getMetadata("noteType"));
        Assertions.assertEquals(1576500142563L, firstPage.getMetadata("createdTime"));
        Assertions.assertEquals(1576500142563L, firstPage.getMetadata("modifiedTime"));
        Assertions.assertEquals(15, firstPage.getMetadata("pageNo"));
        Assertions.assertEquals(5, firstPage.getMetadata("version"));
        Assertions.assertEquals(236.000D, firstPage.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(323.000D, firstPage.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(1, firstPage.getStrokes().size());

        Stroke stroke = firstPage.getStrokes().get(0);
        Assertions.assertEquals(1576499414218L, stroke.getStartTime());
        Assertions.assertEquals(1576499414226L, stroke.getEndTime());
        Assertions.assertEquals(3, stroke.getDots().size());

        Page secondPage = uhc.getPages().get(1);
        Assertions.assertEquals("neo", secondPage.getMetadata("id"));
        Assertions.assertEquals(609, secondPage.getMetadata("noteType"));
        Assertions.assertEquals(1576500142905L, secondPage.getMetadata("createdTime"));
        Assertions.assertEquals(1576500328443L, secondPage.getMetadata("modifiedTime"));
        Assertions.assertEquals(79, secondPage.getMetadata("pageNo"));
        Assertions.assertEquals(5, secondPage.getMetadata("version"));
        Assertions.assertEquals(236.000D, secondPage.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(323.000D, secondPage.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(643, secondPage.getStrokes().size());

        Stroke firstStroke = secondPage.getStrokes().get(0);
        Assertions.assertEquals(1576499042448L, firstStroke.getStartTime());
        Assertions.assertEquals(1576499043432L, firstStroke.getEndTime());
        Assertions.assertEquals(97, firstStroke.getDots().size());
        Assertions.assertEquals(1576499042448L, firstStroke.getDots().get(0).getTimestamp());
        Assertions.assertEquals(28.8984257D, firstStroke.getDots().get(0).getX(), TestUtils.EPSILON);
        Assertions.assertEquals(72.8031712D, firstStroke.getDots().get(0).getY(), TestUtils.EPSILON);

        Stroke lastStroke = secondPage.getStrokes().get(secondPage.getStrokes().size() - 1);
        Assertions.assertEquals(1576500049236L, lastStroke.getStartTime());
        Assertions.assertEquals(1576500049319L, lastStroke.getEndTime());
        Assertions.assertEquals(6, lastStroke.getDots().size());
        Assertions.assertEquals(1576500049236L, lastStroke.getDots().get(0).getTimestamp());
        Assertions.assertEquals(86.1737297D, lastStroke.getDots().get(0).getX(), TestUtils.EPSILON);
        Assertions.assertEquals(280.9002883D, lastStroke.getDots().get(0).getY(), TestUtils.EPSILON);
    }

    @Test
    public void testReadNeoNotesArchiveWithFormatSpecified() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .inputFormat(Format.NEONOTES_ARCHIVE)
                .file(TestUtils.openFileForResource("neonotes/archive/archive.neonotes.zip"));

        Assertions.assertEquals(2, uhc.getPages().size());

        Page firstPage = uhc.getPages().get(0);
        Assertions.assertEquals("neo", firstPage.getMetadata("id"));
        Assertions.assertEquals(609, firstPage.getMetadata("noteType"));
        Assertions.assertEquals(1576500142563L, firstPage.getMetadata("createdTime"));
        Assertions.assertEquals(1576500142563L, firstPage.getMetadata("modifiedTime"));
        Assertions.assertEquals(15, firstPage.getMetadata("pageNo"));
        Assertions.assertEquals(5, firstPage.getMetadata("version"));
        Assertions.assertEquals(236.000D, firstPage.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(323.000D, firstPage.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(1, firstPage.getStrokes().size());

        Stroke stroke = firstPage.getStrokes().get(0);
        Assertions.assertEquals(1576499414218L, stroke.getStartTime());
        Assertions.assertEquals(1576499414226L, stroke.getEndTime());
        Assertions.assertEquals(3, stroke.getDots().size());

        Page secondPage = uhc.getPages().get(1);
        Assertions.assertEquals("neo", secondPage.getMetadata("id"));
        Assertions.assertEquals(609, secondPage.getMetadata("noteType"));
        Assertions.assertEquals(1576500142905L, secondPage.getMetadata("createdTime"));
        Assertions.assertEquals(1576500328443L, secondPage.getMetadata("modifiedTime"));
        Assertions.assertEquals(79, secondPage.getMetadata("pageNo"));
        Assertions.assertEquals(5, secondPage.getMetadata("version"));
        Assertions.assertEquals(236.000D, secondPage.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(323.000D, secondPage.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(643, secondPage.getStrokes().size());

        Stroke firstStroke = secondPage.getStrokes().get(0);
        Assertions.assertEquals(1576499042448L, firstStroke.getStartTime());
        Assertions.assertEquals(1576499043432L, firstStroke.getEndTime());
        Assertions.assertEquals(97, firstStroke.getDots().size());
        Assertions.assertEquals(1576499042448L, firstStroke.getDots().get(0).getTimestamp());
        Assertions.assertEquals(28.8984257D, firstStroke.getDots().get(0).getX(), TestUtils.EPSILON);
        Assertions.assertEquals(72.8031712D, firstStroke.getDots().get(0).getY(), TestUtils.EPSILON);

        Stroke lastStroke = secondPage.getStrokes().get(secondPage.getStrokes().size() - 1);
        Assertions.assertEquals(1576500049236L, lastStroke.getStartTime());
        Assertions.assertEquals(1576500049319L, lastStroke.getEndTime());
        Assertions.assertEquals(6, lastStroke.getDots().size());
        Assertions.assertEquals(1576500049236L, lastStroke.getDots().get(0).getTimestamp());
        Assertions.assertEquals(86.1737297D, lastStroke.getDots().get(0).getX(), TestUtils.EPSILON);
        Assertions.assertEquals(280.9002883D, lastStroke.getDots().get(0).getY(), TestUtils.EPSILON);
    }

    @Test
    public void testReadNeoNotesWithAutoDetect() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("neonotes/single/corners-page.data"));

        Page page = uhc.getPage();

        TestUtils.checkCorners(page);
    }

    @Test
    public void testReadLsPDFWithAutoDetect() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("lspdf/corners-page.pdf"));

        Page page = uhc.getPage();

        TestUtils.checkCorners(page);
    }

    @Test
    public void testReadInkMLWithAutoDetect() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("inkml/single/sample-filled.inkml"));

        Page page = uhc.getPage();

        Assertions.assertEquals(300D, page.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(400D, page.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(2, page.getStrokes().size());

        Stroke firstStroke = page.getStrokes().get(0);
        Assertions.assertEquals(0L, firstStroke.getStartTime());
        Assertions.assertEquals(5000L, firstStroke.getEndTime());
        Assertions.assertEquals(10, firstStroke.getDots().size());

        List<Dot> firstStrokeDots = firstStroke.getDots();
        Assertions.assertEquals(new Dot(2D, 4D, 500L), firstStrokeDots.get(0));
        Assertions.assertEquals(new Dot(4D, 7D, 1000L), firstStrokeDots.get(1));
        Assertions.assertEquals(new Dot(10D, 13D, 1500L), firstStrokeDots.get(2));
        Assertions.assertEquals(new Dot(12D, 14D, 2000L), firstStrokeDots.get(3));
        Assertions.assertEquals(new Dot(15D, 17D, 2500L), firstStrokeDots.get(4));
        Assertions.assertEquals(new Dot(16D, 18D, 3000L), firstStrokeDots.get(5));
        Assertions.assertEquals(new Dot(20D, 22D, 3500L), firstStrokeDots.get(6));
        Assertions.assertEquals(new Dot(20D, 24D, 4000L), firstStrokeDots.get(7));
        Assertions.assertEquals(new Dot(20D, 27D, 4500L), firstStrokeDots.get(8));
        Assertions.assertEquals(new Dot(20D, 28D, 5000L), firstStrokeDots.get(9));

        Stroke secondStroke = page.getStrokes().get(1);
        Assertions.assertEquals(5000L, secondStroke.getStartTime());
        Assertions.assertEquals(10000L, secondStroke.getEndTime());
        Assertions.assertEquals(10, secondStroke.getDots().size());

        List<Dot> secondStrokeDots = secondStroke.getDots();
        Assertions.assertEquals(new Dot(30D, 40D, 5500L), secondStrokeDots.get(0));
        Assertions.assertEquals(new Dot(31D, 40D, 6000L), secondStrokeDots.get(1));
        Assertions.assertEquals(new Dot(32D, 40D, 6500L), secondStrokeDots.get(2));
        Assertions.assertEquals(new Dot(33D, 40D, 7000L), secondStrokeDots.get(3));
        Assertions.assertEquals(new Dot(34D, 40D, 7500L), secondStrokeDots.get(4));
        Assertions.assertEquals(new Dot(35D, 40D, 8000L), secondStrokeDots.get(5));
        Assertions.assertEquals(new Dot(36D, 40D, 8500L), secondStrokeDots.get(6));
        Assertions.assertEquals(new Dot(37D, 40D, 9000L), secondStrokeDots.get(7));
        Assertions.assertEquals(new Dot(38D, 40D, 9500L), secondStrokeDots.get(8));
        Assertions.assertEquals(new Dot(39D, 40D, 10000L), secondStrokeDots.get(9));
    }

    @Test
    public void testReadInkMLWithFormatSpecified() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .inputFormat(Format.INKML)
                .file(TestUtils.openFileForResource("inkml/single/sample-filled.inkml"));

        Page page = uhc.getPage();

        Assertions.assertEquals(300D, page.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(400D, page.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(2, page.getStrokes().size());

        Stroke firstStroke = page.getStrokes().get(0);
        Assertions.assertEquals(0L, firstStroke.getStartTime());
        Assertions.assertEquals(5000L, firstStroke.getEndTime());
        Assertions.assertEquals(10, firstStroke.getDots().size());

        List<Dot> firstStrokeDots = firstStroke.getDots();
        Assertions.assertEquals(new Dot(2D, 4D, 500L), firstStrokeDots.get(0));
        Assertions.assertEquals(new Dot(4D, 7D, 1000L), firstStrokeDots.get(1));
        Assertions.assertEquals(new Dot(10D, 13D, 1500L), firstStrokeDots.get(2));
        Assertions.assertEquals(new Dot(12D, 14D, 2000L), firstStrokeDots.get(3));
        Assertions.assertEquals(new Dot(15D, 17D, 2500L), firstStrokeDots.get(4));
        Assertions.assertEquals(new Dot(16D, 18D, 3000L), firstStrokeDots.get(5));
        Assertions.assertEquals(new Dot(20D, 22D, 3500L), firstStrokeDots.get(6));
        Assertions.assertEquals(new Dot(20D, 24D, 4000L), firstStrokeDots.get(7));
        Assertions.assertEquals(new Dot(20D, 27D, 4500L), firstStrokeDots.get(8));
        Assertions.assertEquals(new Dot(20D, 28D, 5000L), firstStrokeDots.get(9));

        Stroke secondStroke = page.getStrokes().get(1);
        Assertions.assertEquals(5000L, secondStroke.getStartTime());
        Assertions.assertEquals(10000L, secondStroke.getEndTime());
        Assertions.assertEquals(10, secondStroke.getDots().size());

        List<Dot> secondStrokeDots = secondStroke.getDots();
        Assertions.assertEquals(new Dot(30D, 40D, 5500L), secondStrokeDots.get(0));
        Assertions.assertEquals(new Dot(31D, 40D, 6000L), secondStrokeDots.get(1));
        Assertions.assertEquals(new Dot(32D, 40D, 6500L), secondStrokeDots.get(2));
        Assertions.assertEquals(new Dot(33D, 40D, 7000L), secondStrokeDots.get(3));
        Assertions.assertEquals(new Dot(34D, 40D, 7500L), secondStrokeDots.get(4));
        Assertions.assertEquals(new Dot(35D, 40D, 8000L), secondStrokeDots.get(5));
        Assertions.assertEquals(new Dot(36D, 40D, 8500L), secondStrokeDots.get(6));
        Assertions.assertEquals(new Dot(37D, 40D, 9000L), secondStrokeDots.get(7));
        Assertions.assertEquals(new Dot(38D, 40D, 9500L), secondStrokeDots.get(8));
        Assertions.assertEquals(new Dot(39D, 40D, 10000L), secondStrokeDots.get(9));
    }

    @Test
    public void testReadHandSpyWithAutoDetect() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("handspy/single/sample-filled.json"));

        Page page = uhc.getPage();

        Assertions.assertEquals(609, ((BigDecimal) page.getMetadata("noteType")).intValue());
        Assertions.assertEquals(300D, page.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(400D, page.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(2, page.getStrokes().size());

        Stroke firstStroke = page.getStrokes().get(0);
        Assertions.assertEquals(0L, firstStroke.getStartTime());
        Assertions.assertEquals(5000L, firstStroke.getEndTime());
        Assertions.assertEquals(10, firstStroke.getDots().size());

        List<Dot> firstStrokeDots = firstStroke.getDots();
        Assertions.assertEquals(new Dot(2D, 4D, 500L, 0.457D), firstStrokeDots.get(0));
        Assertions.assertEquals(new Dot(4D, 7D, 1000L, 0.658D), firstStrokeDots.get(1));
        Assertions.assertEquals(new Dot(10D, 13D, 1500L, 0.488D), firstStrokeDots.get(2));
        Assertions.assertEquals(new Dot(12D, 14D, 2000L, 0.511D), firstStrokeDots.get(3));
        Assertions.assertEquals(new Dot(15D, 17D, 2500L, 0.774D), firstStrokeDots.get(4));
        Assertions.assertEquals(new Dot(16D, 18D, 3000L, 0.458D), firstStrokeDots.get(5));
        Assertions.assertEquals(new Dot(20D, 22D, 3500L, 0.653D), firstStrokeDots.get(6));
        Assertions.assertEquals(new Dot(20D, 24D, 4000L, 0.754D), firstStrokeDots.get(7));
        Assertions.assertEquals(new Dot(20D, 27D, 4500L, 0.312D), firstStrokeDots.get(8));
        Assertions.assertEquals(new Dot(20D, 28D, 5000L, 0.235D), firstStrokeDots.get(9));

        Stroke secondStroke = page.getStrokes().get(1);
        Assertions.assertEquals(5000L, secondStroke.getStartTime());
        Assertions.assertEquals(10000L, secondStroke.getEndTime());
        Assertions.assertEquals(10, secondStroke.getDots().size());

        List<Dot> secondStrokeDots = secondStroke.getDots();
        Assertions.assertEquals(new Dot(30D, 40D, 5500L, 0.488D), secondStrokeDots.get(0));
        Assertions.assertEquals(new Dot(31D, 40D, 6000L, 0.653D), secondStrokeDots.get(1));
        Assertions.assertEquals(new Dot(32D, 40D, 6500L, 0.235D), secondStrokeDots.get(2));
        Assertions.assertEquals(new Dot(33D, 40D, 7000L, 0.511D), secondStrokeDots.get(3));
        Assertions.assertEquals(new Dot(34D, 40D, 7500L, 0.774D), secondStrokeDots.get(4));
        Assertions.assertEquals(new Dot(35D, 40D, 8000L, 0.458D), secondStrokeDots.get(5));
        Assertions.assertEquals(new Dot(36D, 40D, 8500L, 0.754D), secondStrokeDots.get(6));
        Assertions.assertEquals(new Dot(37D, 40D, 9000L, 0.312D), secondStrokeDots.get(7));
        Assertions.assertEquals(new Dot(38D, 40D, 9500L, 0.658D), secondStrokeDots.get(8));
        Assertions.assertEquals(new Dot(39D, 40D, 10000L, 0.457D), secondStrokeDots.get(9));
    }

    @Test
    public void testReadHandSpyWithFormatSpecified() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .inputFormat(Format.HANDSPY)
                .file(TestUtils.openFileForResource("handspy/single/sample-filled.json"));

        Page page = uhc.getPage();

        Assertions.assertEquals(609, ((BigDecimal) page.getMetadata("noteType")).intValue());
        Assertions.assertEquals(300D, page.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(400D, page.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(2, page.getStrokes().size());

        Stroke firstStroke = page.getStrokes().get(0);
        Assertions.assertEquals(0L, firstStroke.getStartTime());
        Assertions.assertEquals(5000L, firstStroke.getEndTime());
        Assertions.assertEquals(10, firstStroke.getDots().size());

        List<Dot> firstStrokeDots = firstStroke.getDots();
        Assertions.assertEquals(new Dot(2D, 4D, 500L, 0.457D), firstStrokeDots.get(0));
        Assertions.assertEquals(new Dot(4D, 7D, 1000L, 0.658D), firstStrokeDots.get(1));
        Assertions.assertEquals(new Dot(10D, 13D, 1500L, 0.488D), firstStrokeDots.get(2));
        Assertions.assertEquals(new Dot(12D, 14D, 2000L, 0.511D), firstStrokeDots.get(3));
        Assertions.assertEquals(new Dot(15D, 17D, 2500L, 0.774D), firstStrokeDots.get(4));
        Assertions.assertEquals(new Dot(16D, 18D, 3000L, 0.458D), firstStrokeDots.get(5));
        Assertions.assertEquals(new Dot(20D, 22D, 3500L, 0.653D), firstStrokeDots.get(6));
        Assertions.assertEquals(new Dot(20D, 24D, 4000L, 0.754D), firstStrokeDots.get(7));
        Assertions.assertEquals(new Dot(20D, 27D, 4500L, 0.312D), firstStrokeDots.get(8));
        Assertions.assertEquals(new Dot(20D, 28D, 5000L, 0.235D), firstStrokeDots.get(9));

        Stroke secondStroke = page.getStrokes().get(1);
        Assertions.assertEquals(5000L, secondStroke.getStartTime());
        Assertions.assertEquals(10000L, secondStroke.getEndTime());
        Assertions.assertEquals(10, secondStroke.getDots().size());

        List<Dot> secondStrokeDots = secondStroke.getDots();
        Assertions.assertEquals(new Dot(30D, 40D, 5500L, 0.488D), secondStrokeDots.get(0));
        Assertions.assertEquals(new Dot(31D, 40D, 6000L, 0.653D), secondStrokeDots.get(1));
        Assertions.assertEquals(new Dot(32D, 40D, 6500L, 0.235D), secondStrokeDots.get(2));
        Assertions.assertEquals(new Dot(33D, 40D, 7000L, 0.511D), secondStrokeDots.get(3));
        Assertions.assertEquals(new Dot(34D, 40D, 7500L, 0.774D), secondStrokeDots.get(4));
        Assertions.assertEquals(new Dot(35D, 40D, 8000L, 0.458D), secondStrokeDots.get(5));
        Assertions.assertEquals(new Dot(36D, 40D, 8500L, 0.754D), secondStrokeDots.get(6));
        Assertions.assertEquals(new Dot(37D, 40D, 9000L, 0.312D), secondStrokeDots.get(7));
        Assertions.assertEquals(new Dot(38D, 40D, 9500L, 0.658D), secondStrokeDots.get(8));
        Assertions.assertEquals(new Dot(39D, 40D, 10000L, 0.457D), secondStrokeDots.get(9));
    }

    @Test
    public void testReadHandSpyLegacyWithAutoDetect() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("handspy/legacy/single/sample-filled.xml"));

        Page page = uhc.getPage();

        Assertions.assertEquals("000", page.getMetadata("id"));
        Assertions.assertEquals("A", page.getMetadata("noteType"));
        Assertions.assertEquals(1, page.getMetadata("pageNo"));
        Assertions.assertEquals(39D, page.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(40D, page.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(2, page.getStrokes().size());

        Stroke firstStroke = page.getStrokes().get(0);
        Assertions.assertEquals(0L, firstStroke.getStartTime());
        Assertions.assertEquals(5000L, firstStroke.getEndTime());
        Assertions.assertEquals(10, firstStroke.getDots().size());

        List<Dot> firstStrokeDots = firstStroke.getDots();
        Assertions.assertEquals(new Dot(2D, 4D, 500L), firstStrokeDots.get(0));
        Assertions.assertEquals(new Dot(4D, 7D, 1000L), firstStrokeDots.get(1));
        Assertions.assertEquals(new Dot(10D, 13D, 1500L), firstStrokeDots.get(2));
        Assertions.assertEquals(new Dot(12D, 14D, 2000L), firstStrokeDots.get(3));
        Assertions.assertEquals(new Dot(15D, 17D, 2500L), firstStrokeDots.get(4));
        Assertions.assertEquals(new Dot(16D, 18D, 3000L), firstStrokeDots.get(5));
        Assertions.assertEquals(new Dot(20D, 22D, 3500L), firstStrokeDots.get(6));
        Assertions.assertEquals(new Dot(20D, 24D, 4000L), firstStrokeDots.get(7));
        Assertions.assertEquals(new Dot(20D, 27D, 4500L), firstStrokeDots.get(8));
        Assertions.assertEquals(new Dot(20D, 28D, 5000L), firstStrokeDots.get(9));

        Stroke secondStroke = page.getStrokes().get(1);
        Assertions.assertEquals(5000L, secondStroke.getStartTime());
        Assertions.assertEquals(10000L, secondStroke.getEndTime());
        Assertions.assertEquals(10, secondStroke.getDots().size());

        List<Dot> secondStrokeDots = secondStroke.getDots();
        Assertions.assertEquals(new Dot(30D, 40D, 5500L), secondStrokeDots.get(0));
        Assertions.assertEquals(new Dot(31D, 40D, 6000L), secondStrokeDots.get(1));
        Assertions.assertEquals(new Dot(32D, 40D, 6500L), secondStrokeDots.get(2));
        Assertions.assertEquals(new Dot(33D, 40D, 7000L), secondStrokeDots.get(3));
        Assertions.assertEquals(new Dot(34D, 40D, 7500L), secondStrokeDots.get(4));
        Assertions.assertEquals(new Dot(35D, 40D, 8000L), secondStrokeDots.get(5));
        Assertions.assertEquals(new Dot(36D, 40D, 8500L), secondStrokeDots.get(6));
        Assertions.assertEquals(new Dot(37D, 40D, 9000L), secondStrokeDots.get(7));
        Assertions.assertEquals(new Dot(38D, 40D, 9500L), secondStrokeDots.get(8));
        Assertions.assertEquals(new Dot(39D, 40D, 10000L), secondStrokeDots.get(9));
    }

    @Test
    public void testReadHandSpyLegacyWithFormatSpecified() {
        UniversalHandwritingConverter uhc = new UniversalHandwritingConverter()
                .inputFormat(Format.HANDSPY_LEGACY)
                .file(TestUtils.openFileForResource("handspy/legacy/single/sample-filled.xml"));

        Page page = uhc.getPage();

        Assertions.assertEquals("000", page.getMetadata("id"));
        Assertions.assertEquals("A", page.getMetadata("noteType"));
        Assertions.assertEquals(1, page.getMetadata("pageNo"));
        Assertions.assertEquals(39D, page.getWidth(), TestUtils.EPSILON);
        Assertions.assertEquals(40D, page.getHeight(), TestUtils.EPSILON);

        Assertions.assertEquals(2, page.getStrokes().size());

        Stroke firstStroke = page.getStrokes().get(0);
        Assertions.assertEquals(0L, firstStroke.getStartTime());
        Assertions.assertEquals(5000L, firstStroke.getEndTime());
        Assertions.assertEquals(10, firstStroke.getDots().size());

        List<Dot> firstStrokeDots = firstStroke.getDots();
        Assertions.assertEquals(new Dot(2D, 4D, 500L), firstStrokeDots.get(0));
        Assertions.assertEquals(new Dot(4D, 7D, 1000L), firstStrokeDots.get(1));
        Assertions.assertEquals(new Dot(10D, 13D, 1500L), firstStrokeDots.get(2));
        Assertions.assertEquals(new Dot(12D, 14D, 2000L), firstStrokeDots.get(3));
        Assertions.assertEquals(new Dot(15D, 17D, 2500L), firstStrokeDots.get(4));
        Assertions.assertEquals(new Dot(16D, 18D, 3000L), firstStrokeDots.get(5));
        Assertions.assertEquals(new Dot(20D, 22D, 3500L), firstStrokeDots.get(6));
        Assertions.assertEquals(new Dot(20D, 24D, 4000L), firstStrokeDots.get(7));
        Assertions.assertEquals(new Dot(20D, 27D, 4500L), firstStrokeDots.get(8));
        Assertions.assertEquals(new Dot(20D, 28D, 5000L), firstStrokeDots.get(9));

        Stroke secondStroke = page.getStrokes().get(1);
        Assertions.assertEquals(5000L, secondStroke.getStartTime());
        Assertions.assertEquals(10000L, secondStroke.getEndTime());
        Assertions.assertEquals(10, secondStroke.getDots().size());

        List<Dot> secondStrokeDots = secondStroke.getDots();
        Assertions.assertEquals(new Dot(30D, 40D, 5500L), secondStrokeDots.get(0));
        Assertions.assertEquals(new Dot(31D, 40D, 6000L), secondStrokeDots.get(1));
        Assertions.assertEquals(new Dot(32D, 40D, 6500L), secondStrokeDots.get(2));
        Assertions.assertEquals(new Dot(33D, 40D, 7000L), secondStrokeDots.get(3));
        Assertions.assertEquals(new Dot(34D, 40D, 7500L), secondStrokeDots.get(4));
        Assertions.assertEquals(new Dot(35D, 40D, 8000L), secondStrokeDots.get(5));
        Assertions.assertEquals(new Dot(36D, 40D, 8500L), secondStrokeDots.get(6));
        Assertions.assertEquals(new Dot(37D, 40D, 9000L), secondStrokeDots.get(7));
        Assertions.assertEquals(new Dot(38D, 40D, 9500L), secondStrokeDots.get(8));
        Assertions.assertEquals(new Dot(39D, 40D, 10000L), secondStrokeDots.get(9));
    }

    @Test
    public void testWriteSvg() throws Exception {
        new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("neonotes/single/page_full.data"))
                .center()
                .outputFormat(Format.SVG)
                .write(Files.newOutputStream(Paths.get("page.svg")));
    }

    @Test
    public void testWriteMultipleSvg() throws Exception {
        new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("neonotes/archive/soft-lines.neonotes.zip"))
                .center()
                .outputFormat(Format.SVG)
                .write(0, Files.newOutputStream(Paths.get("page-1.svg")))
                .write(1, Files.newOutputStream(Paths.get("page-2.svg")))
                .write(2, Files.newOutputStream(Paths.get("page-3.svg")))
                .write(3, Files.newOutputStream(Paths.get("page-4.svg")));
    }

    @Test
    public void testWriteMariona1Svg() throws Exception {
        new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("neonotes/archive/mariona-1.neonotes.zip"))
                .center()
                .outputFormat(Format.SVG)
                .write(0, Files.newOutputStream(Paths.get("page-1.svg")))
                .write(1, Files.newOutputStream(Paths.get("page-2.svg")))
                .write(2, Files.newOutputStream(Paths.get("page-3.svg")))
                .write(3, Files.newOutputStream(Paths.get("page-4.svg")))
                .write(4, Files.newOutputStream(Paths.get("page-5.svg")))
                .write(5, Files.newOutputStream(Paths.get("page-6.svg")))
                .write(6, Files.newOutputStream(Paths.get("page-7.svg")))
                .write(7, Files.newOutputStream(Paths.get("page-8.svg")))
                .write(8, Files.newOutputStream(Paths.get("page-9.svg")))
                .write(9, Files.newOutputStream(Paths.get("page-10.svg")))
                .write(10, Files.newOutputStream(Paths.get("page-11.svg")))
                .write(11, Files.newOutputStream(Paths.get("page-12.svg")));
    }

    @Test
    public void testWriteHandSpyJson() throws Exception {
        new UniversalHandwritingConverter()
                .file(TestUtils.openFileForResource("neonotes/single/page_full.data"))
                .center()
                .normalize(true, 3)
                .write(Files.newOutputStream(Paths.get("page.json")));
    }
}
