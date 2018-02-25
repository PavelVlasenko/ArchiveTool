package archive.tool.core;

import archive.tool.console.Settings;
import org.junit.Test;

public class CompressorTest {

    @Test
    public void shouldDecompress() throws Exception {
        System.out.println("Start program");

        Settings.inputZipDir = "/home/pavel/test/zip/input";
        Settings.outputZipDir = "/home/pavel/test/zip/output";
        Settings.maxSize = 60;

        Compressor compressor = new Compressor();
        compressor.compress();
    }
}
