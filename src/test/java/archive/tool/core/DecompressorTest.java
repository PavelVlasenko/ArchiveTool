package archive.tool.core;

import archive.tool.console.Settings;
import org.junit.Test;

public class DecompressorTest {

    @Test
    public void shouldDecompress() throws Exception {
        System.out.println("Start program");

        Settings.inputUnzipDir = "/home/pavel/test/zip/output";
        Settings.outputUnzipDir = "/home/pavel/test/zip/outputUnzip";

        Decompressor decompressor = new Decompressor();
        decompressor.decompress();
    }
}
