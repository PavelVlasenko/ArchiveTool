package archive.tool.core.zip;

import archive.tool.console.Settings;
import archive.tool.core.Compressor;
import archive.tool.core.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Compress files
 */
public class ZipCompressor implements Compressor {

    private ZipOutputStream zipOut;
    private String zipPath;
    private ZipEntry zipEntry;
    private int archiveCounter = 0;
    private int fileCounter = 0;

    @Override
    public void compress()  throws IOException {
        nextArchive(true);
        File fileToZip = new File(Settings.inputZipDir);
        compressFile(fileToZip, fileToZip.getName());
        close();
    }

    private void close() throws IOException{
        if(zipOut != null) {
            zipOut.close();
            zipOut = null;
        }
    }

    /**
     * Compress single file.
     * @param fileToZip target file.
     * @param fileName file name.
     * IOException when file not found or error while read/write to file.
     */
    private void compressFile(File fileToZip, String fileName) throws IOException {
        System.out.println("Start Zip file " + fileName);
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                compressFile(childFile, fileName + "/" + childFile.getName());
            }
            return;
        }
        if(zipOut == null) {
            nextArchive(true);
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);

        FileUtil.write(fis, zipOut);

        zipOut.closeEntry();
        fis.close();
        fileCounter++;
        System.out.println("Finish Zip file " + fileName);
        if (isSizeExceeded()) {
            removeEntry(zipPath, fileName);
            if(isFileLargerMaxLimit()) {
                System.out.println("Compressed file " + fileName + " is larger then max limit.");
                if(fileCounter != 0) {
                    System.out.println("Current archive contains files, create new one");
                    nextArchive(true);
                }
                int parts = (int)(zipEntry.getCompressedSize()/Settings.maxSize + 1);
                System.out.println("Split file into " + parts + " parts");
                long partSize = (long) Math.ceil((double)zipEntry.getSize() / parts);
                System.out.println("Part size = " + partSize);
                compressLargeFile(fileToZip, fileName, parts, partSize);
            }
            else {
                System.out.println("Max limit exceeded. Create new archive.");
                nextArchive(true);
                compressFile(fileToZip, fileName);
            }
        }
    }

    /**
     * If file, even if compressed, is larger then maz limit, we must to split it to the parts.
     * And when decompress combine them to the single file.
     *
     * @param fileToZip target file.
     * @param fileName file name.
     * @param parts number of parts for this file.
     * @param partSize part size in bytes.
     * @throws IOException when file not found or error while read/write to file.
     */
    private void compressLargeFile(File fileToZip, String fileName, int parts, long partSize) throws IOException {
        if(zipOut == null) {
            nextArchive(true);
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        long partLimit = partSize;
        long counter = 0;
        for(int i = 0; i < parts; i++) {
            zipEntry = new ZipEntry(fileName + "_part" + i);
            zipOut.putNextEntry(zipEntry);
            int cur;
            while ((counter <= partLimit) && (cur = fis.read()) >= 0) {
                zipOut.write(cur);
                counter++;
            }
            partLimit += partSize;
            if(i!= (parts -1)) nextArchive(true);
        }
        close();
    }

    /**
     * Closes current archive, and open new one.
     * Archive counter is added to archive name, so order is dirCompressed0, dirCompressed1, etc...
     *
     * @param resetFileCounter if true, resets file counter
     * @throws IOException if file not found.
     */
    private void nextArchive(boolean resetFileCounter) throws IOException {
        if (zipOut != null) {
            zipOut.close();
        }
        zipPath = Settings.outputZipDir + File.separator + "dirCompressed" + archiveCounter + ".zip";
        zipOut = new ZipOutputStream(new FileOutputStream(zipPath));
        archiveCounter++;
        if (resetFileCounter) {
            fileCounter = 0;
        } /*else {
            fileCounter--;
        }*/
    }

    /**
     * Check archive size.
     * @return true if archive size is exceeded, false otherwise.
     */
    private boolean isSizeExceeded() {
        File zip = new File(zipPath);
        long zipSize = zip.length();
        boolean result =  zipSize > Settings.maxSize;
        System.out.println(zipPath + " size =  " + zipSize + ", limit exceeded = " + result);
        return result;
    }

    /**
     * Compare compressed file size and max limit.
     * @return true if file, even compressed is larger then max size limit.
     */
    private boolean isFileLargerMaxLimit() {
        long entrySize = zipEntry.getCompressedSize();
        return entrySize > Settings.maxSize;
    }

    /**
     * Removes file from zip archive.
     *
     * @param zipPath path to zip file
     * @param entryName file name
     * @throws IOException if file not found or error while read/write.
     */
    private void removeEntry(String zipPath, String entryName) throws IOException {
        zipOut.close();
        nextArchive(false);
        System.out.println("Remove entry " + entryName + " from zip " + zipPath);
        File zipFile = new File(zipPath);

        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry = zin.getNextEntry();

        while (entry != null) {
            String name = entry.getName();
            if (!entryName.equals(name)) {
                zipOut.putNextEntry(new ZipEntry(name));
                FileUtil.write(zin, zipOut);
            } else {
                System.out.println("Skip entry " + name);
            }
            entry = zin.getNextEntry();
        }
        zin.close();
        zipFile.delete();
    }
}
