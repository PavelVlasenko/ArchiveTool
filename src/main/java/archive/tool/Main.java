package archive.tool;

import archive.tool.console.Action;
import archive.tool.console.Console;
import archive.tool.console.Settings;
import archive.tool.core.Compressor;
import archive.tool.core.Decompressor;
import archive.tool.core.impl.zip.ZipCompressor;
import archive.tool.core.impl.zip.ZipDecompressor;

public class Main {

    public static void main(String ... args) throws Exception {
        System.out.println("Start program");

        Console console = new Console();
        console.enterSettings();
        if(Settings.action == Action.COMPRESS) {
            Compressor compressor = new ZipCompressor();
            compressor.compress();
        } else {
            Decompressor decompressor = new ZipDecompressor();
            decompressor.decompress();
        }

    }
}
