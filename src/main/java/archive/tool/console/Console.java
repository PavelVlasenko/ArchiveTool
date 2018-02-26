package archive.tool.console;

import java.util.Scanner;

/**
 * Setups tool settings.
 */
public class Console {

    public void enterSettings() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n Select an action(enter 0 or 1):");
        System.out.println("[0] Compress");
        System.out.println("[1] Decompress");

        int action = scanner.nextInt();
        switch(action) {
            case 0: Settings.action = Action.COMPRESS;
                    enterCompressSettings();
                    break;
            case 1: Settings.action = Action.DECOMPRESS;
                    enterDecompressSettings();
                    break;
            default: System.out.println("Incorrect action, enter 0 or 1"); System.exit(0);
        }
    }

    private void enterCompressSettings() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n Enter path to Input directory:");
        String inputDir = scanner.next();
        Settings.inputZipDir = inputDir;

        System.out.println("\n Enter path to Output directory:");
        String outputDir = scanner.next();
        Settings.outputZipDir = outputDir;

        System.out.println("\n Enter max output file size in bytes:");
        Long maxSize = scanner.nextLong();
        Settings.maxSize = maxSize;
    }

    private void enterDecompressSettings() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n Enter path to Input directory:");
        String inputDir = scanner.next();
        Settings.inputUnzipDir = inputDir;

        System.out.println("\n Enter path to Output directory:");
        String outputDir = scanner.next();
        Settings.outputUnzipDir = outputDir;
    }
}
