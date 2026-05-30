package dk.simwir.musicbox.reader;

import dk.simwir.musicbox.logging.LogUtil;

import java.util.Scanner;
import java.util.logging.Logger;

public class StandardReader implements IdReader {

    private final Scanner scanner = new Scanner(System.in);
    private static final Logger logger = LogUtil.getLogger("reader.StandardReader");

    @Override
    public Id read() {
        logger.entering("StandardReader", "read");
        System.out.print("Input>");
        String input = scanner.nextLine();
        logger.info(() -> String.format("Input: %s", input));
        return new Id(input);
    }
}
