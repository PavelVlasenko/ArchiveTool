package archive.tool.core;

import archive.tool.console.Settings;
import org.junit.Test;

public class CompressorTest {

    @Test
    public void shouldDecompress() throws Exception {
        System.out.println("Start program");

        Settings.inputZipDir = "C:/Users/SBT-Vlasenko-PV/Test/zip/in";
        Settings.outputZipDir = "C:/Users/SBT-Vlasenko-PV/Test/zip/out";
        Settings.maxSize = 1000;

        Compressor compressor = new Compressor();
        compressor.compress();
    }
}
