package archive.tool.core;

import archive.tool.console.Settings;
import org.junit.Test;

public class DecompressorTest {

    @Test
    public void shouldDecompress() throws Exception {
        System.out.println("Start program");

        Settings.inputUnzipDir = "C:/Users/SBT-Vlasenko-PV/Test/zip/out";
        Settings.outputUnzipDir = "C:\\Users\\SBT-Vlasenko-PV\\Test\\zip\\unzipOut";

        Decompressor decompressor = new Decompressor();
        decompressor.decompress();
    }
}
