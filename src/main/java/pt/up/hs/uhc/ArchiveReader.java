package pt.up.hs.uhc;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import pt.up.hs.uhc.models.Page;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Interface implemented by readers of archives.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface ArchiveReader {

    /**
     * Reads an archive of handwriting data, provided an {@link InputStream} of
     * its contents. For instance, in Neo Notes it would be an
     * {@link InputStream} to a "xxx.neonotes" file.
     *
     * @param zis {@link ZipInputStream} to an archive of handwriting data.
     * @return {@link List<Page>} data from archive.
     * @throws Exception if an exception occurs while reading data.
     */
    List<Page> read(ZipInputStream zis) throws Exception;

    /**
     * Reads an archive of handwriting data, provided an {@link InputStream} of
     * its contents. For instance, in Neo Notes it would be an
     * {@link InputStream} to a "xxx.neonotes" file.
     *
     * @param tais {@link TarArchiveInputStream} to an archive of handwriting data.
     * @return {@link List<Page>} data from archive.
     * @throws Exception if an exception occurs while reading data.
     */
    List<Page> read(TarArchiveInputStream tais) throws Exception;
}
