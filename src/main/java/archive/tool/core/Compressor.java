package archive.tool.core;

import archive.tool.console.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Compressor {

    private ZipOutputStream zipOut;
    private String zipPath;
    private ZipEntry zipEntry;
    private int archiveCounter = 0;
    private int fileCounter = 0;

    public void compress() throws Exception {
        nextArchive();
        File fileToZip = new File(Settings.inputZipDir);
        zipFile(fileToZip, fileToZip.getName());
        close();
    }

    private void close() throws Exception {
        if(zipOut != null) {
            zipOut.close();
            zipOut = null;
        }
    }

    public void zipFile(File fileToZip, String fileName) throws Exception {
        System.out.println("Start Zip file " + fileName);
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName());
            }
            return;
        }
        if(zipOut == null) {
            nextArchive();
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        int currentByte;
        while ((currentByte = fis.read()) >= 0) {
            zipOut.write(currentByte);
        }
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
                    nextArchive();
                }
                int parts = (int)(zipEntry.getCompressedSize()/Settings.maxSize + 1);
                System.out.println("Split file into " + parts + " parts");
                long partSize = (long) Math.ceil((double)zipEntry.getSize() / parts);
                System.out.println("Part size = " + partSize);
                compressLargeFile(fileToZip, fileName, parts, partSize);
            }
            else {
                System.out.println("Max limit exceeded. Create new archive.");
                nextArchive();
                zipFile(fileToZip, fileName);
            }
        }
    }

    private void compressLargeFile(File fileToZip, String fileName, int parts, long partSize) throws Exception {
        if(zipOut == null) {
            nextArchive();
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        long partLimit = partSize;
        long counter = 0;
        for(int i = 0; i < parts; i++) {
            zipEntry = new ZipEntry(fileName + "_part" + i);
            zipOut.putNextEntry(zipEntry);
            int cur = 0;
            while ((counter <= partLimit) && (cur = fis.read()) >= 0) {
                zipOut.write(cur);
                counter++;
            }
            partLimit += partSize;
            if(i!= (parts -1)) nextArchive();
        }
        zipOut.close();
        zipOut = null;
    }

    private void nextArchive() throws Exception {
        if (zipOut != null) {
            zipOut.close();
        }
        zipPath = Settings.outputZipDir + File.separator + "dirCompressed" + archiveCounter + ".zip";
        zipOut = new ZipOutputStream(new FileOutputStream(zipPath));
        archiveCounter++;
        fileCounter = 0;
    }

    private boolean isSizeExceeded() throws Exception {
        File zip = new File(zipPath);
        long zipSize = zip.length();
        boolean result =  zipSize > Settings.maxSize;
        System.out.println(zipPath + " size =  " + zipSize + ", limit exceeded = " + result);
        return result;
    }

    private boolean isFileLargerMaxLimit() {
        long entrySize = zipEntry.getCompressedSize();
        return entrySize > Settings.maxSize;
    }

    private void removeEntry(String zipPath, String entryName) throws Exception {
        zipOut.close();
        nextArchive();
        System.out.println("Remove entry " + entryName + " from zip " + zipPath);
        File zipFile = new File(zipPath);

        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry = zin.getNextEntry();

        byte[] buf = new byte[1024];
        while (entry != null) {
            String name = entry.getName();
            if (!entryName.equals(name)) {
                zipOut.putNextEntry(new ZipEntry(name));
                int len;
                while ((len = zin.read(buf)) > 0) {
                    zipOut.write(buf, 0, len);
                }
            } else {
                System.out.println("Skip entry " + name);
            }
            entry = zin.getNextEntry();
        }
        zin.close();
        zipFile.delete();
    }
}
