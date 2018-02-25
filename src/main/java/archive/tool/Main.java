package archive.tool;

import archive.tool.core.Compressor;

public class Main {

    public static void main(String ... args) throws Exception {
        System.out.println("Start program");

        Settings.inputDir = "/home/pavel/test/zip/input";
        Settings.outputDir = "/home/pavel/test/zip/output";
        Settings.maxSize = 600;

        Compressor compressor = new Compressor();
        compressor.compress();

    }
}
