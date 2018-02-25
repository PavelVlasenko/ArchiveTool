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
            zipOut.close();
            removeEntry(zipPath, fileName);
            zipPath = Settings.outputZipDir + File.separator + "dirCompressed" + archiveCounter + ".zip";
            FileOutputStream fos = new FileOutputStream(zipPath);
            zipOut = new ZipOutputStream(fos);
            archiveCounter ++;
            zipFile(fileToZip, fileName);
        }
    }

    private boolean isSizeExceeded() throws Exception {
        long s = zipEntry.getCompressedSize();
        File zip = new File(zipPath);
        long h = zip.length();
        return h > Settings.maxSize;
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
