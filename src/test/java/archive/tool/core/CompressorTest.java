package archive.tool.core;

import archive.tool.console.Settings;
import archive.tool.core.impl.zip.ZipCompressor;
import org.junit.Test;

public class CompressorTest {

    @Test
    public void shouldCompress() throws Exception {
        System.out.println("Start compress test");

        Settings.inputCompressDir = "C:/Users/SBT-Vlasenko-PV/Test/zip/in";
        Settings.outputCompressDir = "C:/Users/SBT-Vlasenko-PV/Test/zip/out";
        Settings.maxSize = 1048576;

        ZipCompressor compressor = new ZipCompressor();
        compressor.compress();
        System.out.println("Done");
    }
}
