package archive.tool.core.impl.zip;

import archive.tool.console.Settings;
import archive.tool.core.FileUtil;
import archive.tool.core.impl.AbstractCompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipCompressor extends AbstractCompressor<ZipOutputStream> {

    private ZipEntry zipEntry;

    @Override
    protected void compressFile(File fileToZip, String fileName) throws IOException {
        System.out.println("Start Zip file " + fileName);
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                compressFile(childFile, fileName + "/" + childFile.getName());
            }
            return;
        }
        if(out == null) {
            nextArchive(true);
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        zipEntry = new ZipEntry(fileName);
        out.putNextEntry(zipEntry);

        FileUtil.write(fis, out);

        out.closeEntry();
        fis.close();
        fileCounter++;
        System.out.println("Finish Zip file " + fileName);
        if (isSizeExceeded()) {
            removeEntry(archivePath, fileName);
            if(isFileLargerMaxLimit()) {
                System.out.println("Compressed file " + fileName + " is larger then max limit.");
                if(fileCounter != 0) {
                    System.out.println("Current archive contains files, create new one");
                    nextArchive(true);
                }
                int parts = (int)(zipEntry.getCompressedSize()/(Settings.maxSize - cellSize) + 1);
                System.out.println("Split file into " + parts + " parts");
                long partSize = (long) Math.ceil((double)zipEntry.getSize() / parts);
                System.out.println("Part size = " + partSize);
                LargeFileZipCompressor largeFileZipCompressor = new LargeFileZipCompressor(fileToZip, fileName, parts, partSize, out, zipEntry, archiveCounter -1);
                largeFileZipCompressor.start();
                largeFilesThreads.add(largeFileZipCompressor);
                out = null;
            }
            else {
                System.out.println("Max limit exceeded. Create new archive.");
                nextArchive(true);
                compressFile(fileToZip, fileName);
            }
        }
    }

    @Override
    protected ZipOutputStream initOutputStream() throws IOException {
        return new ZipOutputStream(new FileOutputStream(archivePath));
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
        out.close();
        nextArchive(false);
        System.out.println("Remove entry " + entryName + " from zip " + zipPath);
        File zipFile = new File(zipPath);

        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry = zin.getNextEntry();

        while (entry != null) {
            String name = entry.getName();
            if (!entryName.equals(name)) {
                out.putNextEntry(new ZipEntry(name));
                FileUtil.write(zin, out);
            } else {
                System.out.println("Skip entry " + name);
            }
            entry = zin.getNextEntry();
        }
        zin.close();
        zipFile.delete();
    }
}
