package archive.tool.core;

import archive.tool.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compressor {

    private int sizeCounter;
    private int archiveCounter;
    private int fileCounter;
    private String currentFileName;
    private ZipOutputStream zipOut;

    public void compress() throws Exception {
        FileOutputStream fos = new FileOutputStream(Settings.outputDir + File.separator + "dirCompressed.zip");
        zipOut = new ZipOutputStream(fos);
        zipOut.setLevel(2);
        File fileToZip = new File(Settings.inputDir);

        zipFile(fileToZip, fileToZip.getName());
        zipOut.close();
        fos.close();
    }

    public void zipFile(File fileToZip, String fileName) throws Exception {
        currentFileName = fileName;
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName());
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        int currentByte;
        while ((currentByte = fis.read()) >= 0) {
            sizeCounter++;
            if(sizeCounter == 20000) {
                Integer I = 0;
            }
            if(sizeCounter > Settings.maxSize) {
                System.out.println("Max limit is reached");
                sizeCounter = 0;
                archiveCounter++;
                zipOut.close();

                FileOutputStream fos = new FileOutputStream(Settings.outputDir + File.separator + "dirCompressed" + archiveCounter + ".zip");
                zipOut = new ZipOutputStream(fos);
                zipOut.setLevel(2);
                if(fileName.equals(currentFileName)) {
                    fileCounter++;
                } else {
                    fileCounter = 1;
                }
                ZipEntry newZipEntry = new ZipEntry(fileName + fileCounter);
                zipOut.putNextEntry(newZipEntry);
            }
            zipOut.write(currentByte);
            zipOut.flush();
        }
        fis.close();
    }


}
