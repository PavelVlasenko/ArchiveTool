package archive.tool.core.impl.zip;

import archive.tool.console.Settings;
import archive.tool.core.FileUtil;
import archive.tool.core.impl.AbstractCompressor;
import archive.tool.core.impl.AbstractDecompressor;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Decompress files
 */
public class ZipDecompressor extends AbstractDecompressor {

    protected void decompressArchive(File file) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        ZipEntry ze = zis.getNextEntry();
        while(ze!=null){
            String fileName = ze.getName();
            if(fileName.contains(AbstractCompressor.PART_DELIMETER)) {
                System.out.println("Part of large file " + fileName + " found");
                largeFiles.put(fileName, file);
                ze = zis.getNextEntry();
                continue;
            }
            File newFile = new File(Settings.outputUncompressDir + File.separator + fileName);
            System.out.println("Decompress : " + newFile.getAbsoluteFile());

            new File(newFile.getParent()).mkdirs();

            FileOutputStream fos = new FileOutputStream(newFile);

            FileUtil.write(zis, fos);
            fos.close();
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        System.out.println("Done");
    }

    protected  void decompressLargeFiles() throws IOException {
        if(largeFiles.isEmpty()) {
            return;
        }
        for(Map.Entry<String, File> entry : largeFiles.entrySet()) {
            String fileName = entry.getKey();
            String shortFileName = Settings.outputUncompressDir + File.separator +
                    fileName.substring(0, fileName.indexOf(AbstractCompressor.PART_DELIMETER));
            FileOutputStream fos;
            if(!largeFilesOutputStreams.keySet().contains(shortFileName)) {
                File dir = new File(shortFileName).getParentFile();
                if(!dir.exists()) {
                    boolean mkdir = dir.mkdirs();
                    if(!mkdir) {
                        throw new RuntimeException("Cannot create directory for " + shortFileName);
                    }
                }
                fos = new FileOutputStream(shortFileName);
                largeFilesOutputStreams.put(shortFileName, fos);
            } else {
                fos = largeFilesOutputStreams.get(shortFileName);
            }

            ZipFile zipFile = new ZipFile(entry.getValue());
            ZipEntry zipEntry = zipFile.getEntry(fileName);
            InputStream zis = zipFile.getInputStream(zipEntry);
            FileUtil.write(zis, fos);
        }
    }
}
