package archive.tool.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    /**
     *
     * @param is InputStream.
     * @param os OutputStream.
     * @throws IOException While read/write
     */
    public static void write(InputStream is, OutputStream os) throws IOException {
        int len;
        byte[] buffer = new byte[1024];
        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
    }
}
