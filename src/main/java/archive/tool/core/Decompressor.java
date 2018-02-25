package archive.tool.core;

import archive.tool.console.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Decompressor {

    private Map<String, File> largeFilesIn = new TreeMap<>();
    private Map<String, FileOutputStream> largeFilesOut = new HashMap<>();

    public void decompress() throws Exception {
        System.out.println("Start decompress archives.");
        File inputDir = new File(Settings.inputUnzipDir);
        File[] archives = inputDir.listFiles();
        for (File archive : archives) {
            decompressArchive(archive);
        }
        decompressLargeFiles();
    }

    private void decompressArchive(File file) throws Exception {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        ZipEntry ze = zis.getNextEntry();
        while(ze!=null){
            String fileName = ze.getName();
            if(fileName.contains("_part")) {
                System.out.println("Large file input for file " + fileName);
                largeFilesIn.put(fileName, file);
                continue;
            }

            File newFile = new File(Settings.outputUnzipDir + File.separator + fileName);

            System.out.println("file unzip : " + newFile.getAbsoluteFile());

            new File(newFile.getParent()).mkdirs();

            FileOutputStream fos = new FileOutputStream(newFile);

            int len;
            byte[] buffer = new byte[1024];
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();
            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();

        System.out.println("Done");
    }

    private void decompressLargeFiles() throws Exception {
        if(largeFilesIn.isEmpty()) {
            return;
        }
        for(Map.Entry<String, File> entry : largeFilesIn.entrySet()) {
            String fileName = entry.getKey();
            String shortFileName = Settings.outputUnzipDir + File.separator +
                    fileName.substring(0, fileName.indexOf("_part"));
            FileOutputStream fos = null;
            if(!largeFilesOut.keySet().contains(shortFileName)) {
                fos = new FileOutputStream(shortFileName);
                largeFilesOut.put(shortFileName, fos);
            } else {
                fos = largeFilesOut.get(shortFileName);
            }

            ZipFile zipFile = new ZipFile(entry.getKey());
            ZipEntry zipEntry = zipFile.getEntry(fileName);
            InputStream zis = zipFile.getInputStream(zipEntry);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }
}
