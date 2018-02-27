package archive.tool.core.impl;

import archive.tool.console.Settings;
import archive.tool.core.Decompressor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractDecompressor  implements Decompressor {

    protected Map<String, File> largeFiles = new TreeMap<>((o1, o2) -> {
        String partDelimeter = AbstractCompressor.PART_DELIMETER;
        String nameFile1 = o1.substring(0, o1.indexOf(partDelimeter));
        String nameFile2 = o2.substring(0, o2.indexOf(partDelimeter));
        Integer partFile1 = Integer.valueOf(o1.substring(o1.indexOf(partDelimeter) + partDelimeter.length()));
        Integer partFile2 = Integer.valueOf(o2.substring(o2.indexOf(partDelimeter) + partDelimeter.length()));

        if (nameFile1.compareTo(nameFile2) == 0) {
            return partFile1.compareTo(partFile2);
        } else {
            return nameFile1.compareTo(nameFile2);
        }
    });
    protected Map<String, FileOutputStream> largeFilesOutputStreams = new HashMap<>();

    /**
     * Decompress files. If archives contains splitted large files, stores
     * names and sorts by parts in map. And then decompress them from first part to the last one.
     */
    public void decompress() throws IOException {
        System.out.println("Start decompress archives.");
        File inputDir = new File(Settings.inputUncompressDir);
        File[] archives = inputDir.listFiles();
        for (File archive : archives) {
            decompressArchive(archive);
        }
        decompressLargeFiles();
    }

    /**
     * Decompress single archive.
     * @param file target archive.
     * @throws IOException when file not found or error while read/write to file.
     */
    protected abstract void decompressArchive(File file) throws IOException;

    /**
     * Decompress large files.
     * @throws IOException when file not found or error while read/write to file.
     */
    protected abstract void decompressLargeFiles() throws IOException;
}
