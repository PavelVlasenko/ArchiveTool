package archive.tool.core;

import archive.tool.console.Settings;
import archive.tool.core.impl.ZipCompressor;
import org.junit.Test;

public class CompressorTest {

    @Test
    public void shouldDecompress() throws Exception {
        System.out.println("Start program");

        Settings.inputZipDir = "C:/Users/SBT-Vlasenko-PV/Test/zip/in";
        Settings.outputZipDir = "C:/Users/SBT-Vlasenko-PV/Test/zip/out";
        Settings.maxSize = 2000;

        ZipCompressor compressor = new ZipCompressor();
        compressor.compress();
    }
}
