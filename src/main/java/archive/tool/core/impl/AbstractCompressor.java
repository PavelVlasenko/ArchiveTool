package archive.tool.core.impl;

import archive.tool.console.Settings;
import archive.tool.core.Compressor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Compress files
 */
public abstract class AbstractCompressor<T extends OutputStream> implements Compressor {

    public static final String PART_DELIMETER = "_part";
    protected long cellSize = 4000;
    protected T out;
    protected String archivePath;
    protected int fileCounter = 0;
    private int archiveCounter = 0;

    @Override
    public void compress()  throws IOException {
        nextArchive(true);
        File fileToZip = new File(Settings.inputCompressDir);
        compressFile(fileToZip, fileToZip.getName());
        close();
    }

    /**
     * Closes current archive, and open new one.
     * Archive counter is added to archive name, so order is dirCompressed0, dirCompressed1, etc...
     *
     * @param resetFileCounter if true, resets file counter
     * @throws IOException if file not found.
     */
    protected void nextArchive(boolean resetFileCounter) throws IOException {
        close();
        archivePath = Settings.outputCompressDir + File.separator + "dirCompressed" + archiveCounter + ".zip";
        out = initOutputStream();
        archiveCounter++;
        if (resetFileCounter) {
            fileCounter = 0;
        } else {
            fileCounter--;
        }
    }

    /**
     * Check archive size.
     * @return true if archive size is exceeded, false otherwise.
     */
    protected boolean isSizeExceeded() {
        File zip = new File(archivePath);
        long zipSize = zip.length();
        boolean result =  zipSize > Settings.maxSize;
        System.out.println(archivePath + " size =  " + zipSize + ", limit exceeded = " + result);
        return result;
    }

    protected void close() throws IOException{
        if(out != null) {
            out.close();
            out = null;
        }
    }

    /**
     * Compress single file.
     * @param fileToZip target file.
     * @param fileName file name.
     * IOException when file not found or error while read/write to file.
     */
    protected abstract void compressFile(File fileToZip, String fileName) throws IOException;

    protected abstract T initOutputStream() throws IOException;

}
