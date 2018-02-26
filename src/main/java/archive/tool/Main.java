package archive.tool;

import archive.tool.console.Settings;
import archive.tool.core.impl.ZipCompressor;

public class Main {

    public static void main(String ... args) throws Exception {
        System.out.println("Start program");

        Settings.inputZipDir = "/home/pavel/test/zip/input";
        Settings.outputZipDir = "/home/pavel/test/zip/output";
        Settings.maxSize = 600;

        ZipCompressor compressor = new ZipCompressor();
        compressor.compress();

    }
}
