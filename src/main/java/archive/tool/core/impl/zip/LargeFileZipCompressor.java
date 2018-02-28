package archive.tool.core.impl.zip;

import archive.tool.console.Settings;
import archive.tool.core.impl.AbstractCompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LargeFileZipCompressor extends Thread {

    private ZipOutputStream out;
    private ZipEntry zipEntry;
    private int archiveCounter;

    private File fileToZip;
    private String fileName;
    private int parts;
    private long partSize;
    private int largeCounter;

    public LargeFileZipCompressor(File fileToZip, String fileName, int parts,
                                  long partSize, ZipOutputStream out, ZipEntry zipEntry, int largeCounter) {
        System.out.println("Create large files compressor: largeCounter = " + largeCounter + ", fileName = " + fileName);
        this.fileToZip = fileToZip;
        this.fileName = fileName;
        this.parts = parts;
        this.partSize = partSize;
        this.out = out;
        this.zipEntry = zipEntry;
        this.largeCounter = largeCounter;
    }

    @Override
    public void run() {
        System.out.println("Create new thread " + Thread.currentThread() + " for file " + zipEntry.getName());
        try {
            compressLargeFile();
        }
        catch (Exception e) {
            System.out.println("Error while compressing large file");
            e.printStackTrace();
        }
    }

    /**
     * If file, even if compressed, is larger then maz limit, we must to split it to the parts.
     * And when decompress combine them to the single file.
     *
     * @throws IOException when file not found or error while read/write to file.
     */
    private void compressLargeFile() throws IOException {
        if(out == null) {
            nextArchive();
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        long partLimit = partSize;
        long counter = 0;
        for(int i = 0; i < parts; i++) {
            zipEntry = new ZipEntry(fileName + AbstractCompressor.PART_DELIMETER + i);
            out.putNextEntry(zipEntry);
            int cur;
            while ((counter <= partLimit) && (cur = fis.read()) >= 0) {
                out.write(cur);
                counter++;
            }
            partLimit += partSize;
            if(i!= (parts -1)) nextArchive();
        }
        out.close();
    }

    private void nextArchive() throws IOException {
        out.close();
        String archivePath = Settings.outputCompressDir + File.separator + "dirCompressed" + largeCounter + " _"
                + archiveCounter + ".zip";
        out = new ZipOutputStream(new FileOutputStream(archivePath));
        archiveCounter++;
    }


}
