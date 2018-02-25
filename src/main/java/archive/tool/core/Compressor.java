package archive.tool.core;

import archive.tool.console.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compressor {

    private ZipOutputStream zipOut;
    private String zipPath;
    private ZipEntry zipEntry;
    private int archiveCounter = 1;

    public void compress() throws Exception {
        zipPath = Settings.outputZipDir + File.separator + "dirCompressed.zip";
        FileOutputStream fos = new FileOutputStream(zipPath);
        zipOut = new ZipOutputStream(fos);

        File fileToZip = new File(Settings.inputZipDir);
        zipFile(fileToZip, fileToZip.getName());
        zipOut.close();
        fos.close();
    }

    public void zipFile(File fileToZip, String fileName) throws Exception {
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName());
            }
            return;
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
        if (isSizeExceeded()) {
            if(isFileLargerMaxLimit()) {
                System.out.println("Compressed file is larger then max limit.");
                nextArchive();
                int parts = (int)(zipEntry.getCompressedSize()/Settings.maxSize + 1);
                System.out.println("Split file into " + parts + " parts");
                long partSize = zipEntry.getSize() * zipEntry.getCompressedSize() / Settings.maxSize;
                System.out.println("Part size = " + partSize);
                compressLargeFile(fileToZip, fileName, parts, partSize);
            }
            else {
                System.out.println("Max limit exceeded. Create new archive.");
                zipOut.close();
                removeEntry(zipPath, fileName);
                nextArchive();
                zipFile(fileToZip, fileName);
            }
        }
    }

    private void compressLargeFile(File fileToZip, String fileName, int parts, long partSize) throws Exception {
        FileInputStream fis = new FileInputStream(fileToZip);
        long partLimit = partSize;
        for(int i = 0; i < parts; i++) {
            zipEntry = new ZipEntry(fileName + "_part" + i);
            zipOut.putNextEntry(zipEntry);
            int currentByte;
            while ((currentByte = fis.read()) >= partLimit) {
                zipOut.write(currentByte);
            }
            partLimit+=partSize;
            zipOut.close();
            nextArchive();
        }
    }

    private void nextArchive() throws Exception {
        zipPath = Settings.outputZipDir + File.separator + "dirCompressed" + archiveCounter + ".zip";
        FileOutputStream fos = new FileOutputStream(zipPath);
        zipOut = new ZipOutputStream(fos);
        archiveCounter++;
    }

    private boolean isSizeExceeded() throws Exception {
        File zip = new File(zipPath);
        long zipSize = zip.length();
        return zipSize > Settings.maxSize;
    }

    private boolean isFileLargerMaxLimit() {
        long entrySize = zipEntry.getCompressedSize();
        return entrySize > Settings.maxSize;
    }

    private void removeEntry(String zipPath, String entryPath) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("create", "false");

        URI uri = URI.create("jar:file:///" + zipPath); // Zip file path

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Files.delete(zipfs.getPath(entryPath)); // File inside zip to delete
        }
    }
}
