package archive.tool.core;

import archive.tool.console.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompressor {

    public void decompress() throws Exception {
        System.out.println("Start decompress archives.");
        File inputDir = new File(Settings.inputUnzipDir);
        File[] archives = inputDir.listFiles();
        for (File archive : archives) {
            decompressArchive(archive);
        }
    }

    private void decompressArchive(File file) throws Exception {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));


        ZipEntry ze = zis.getNextEntry();

        while(ze!=null){

            String fileName = ze.getName();
            File newFile = new File(Settings.outputUnzipDir + File.separator + fileName);

            System.out.println("file unzip : "+ newFile.getAbsoluteFile());

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
}
