package archive.tool.core;

import archive.tool.console.Settings;
import archive.tool.core.impl.zip.ZipDecompressor;
import org.junit.Test;

public class DecompressorTest {

    @Test
    public void shouldDecompress() throws Exception {
        System.out.println("Start decompresstest");

        Settings.inputUncompressDir = "C:/Users/SBT-Vlasenko-PV/Test/zip/out";
        Settings.outputUncompressDir = "C:\\Users\\SBT-Vlasenko-PV\\Test\\zip\\unzipOut";

        ZipDecompressor decompressor = new ZipDecompressor();
        decompressor.decompress();
        System.out.println("Done");
    }
}
