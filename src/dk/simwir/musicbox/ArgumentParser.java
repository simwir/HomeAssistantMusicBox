package dk.simwir.musicbox;

import dk.simwir.musicbox.logging.LogUtil;
import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class ArgumentParser {

    public static final String HA_TOKEN = "ha_token";
    public static final String SONG_FILE = "song_file";
    public static final String URL_OPTION = "url";
    public static final String ENTITY = "entity";

    public record Arguments(String haToken, File actionFile, URL url, String entity) {}

    private static Arguments arguments;
    private static final Logger logger = LogUtil.getLogger("ArgumentParser");

    public static Arguments parseArgs(String[] args) throws IOException, ParseException {
        Options options = new Options();

        options.addOption(getTokenOption())
                .addOption(getActionFileOption())
                .addOption(getUrlOption())
                .addOption(getEntityOption());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = HelpFormatter.builder().get();

        try {
            logger.info("Parsing arguments.");
            CommandLine cmd = parser.parse(options, args);
            parseArguments(cmd);
            return arguments;
        } catch (ParseException e) {
            formatter.printHelp("java -jar yourapp.jar", "header", options, "footer", true);
            throw e;
        }
    }

    private static void parseArguments(CommandLine cmd) throws ParseException {
        try {
            arguments = new Arguments(
                    getOptionValue(cmd, HA_TOKEN),
                    new File(getOptionValue(cmd, SONG_FILE)),
                    new URI(getOptionValue(cmd, URL_OPTION)).toURL(),
                    getOptionValue(cmd, ENTITY)
            );

        } catch (URISyntaxException | MalformedURLException e) {
            logger.severe("Unable to parse URL");
            throw new ParseException(e);
        }
    }

    private static String getOptionValue(CommandLine cmd, String optionName) throws ParseException {
        String optionValue = cmd.getOptionValue(optionName);
        if (isNullOrEmpty(optionValue)) {
            throw new ParseException(optionName + " is empty");
        }
        return optionValue;
    }

    private static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    private static Option getTokenOption() {
        return Option.builder("t")
                .longOpt(HA_TOKEN)
                .hasArg()
                .argName("TOKEN")
                .desc("Home Assistant token (required)")
                .required()
                .get();
    }

    private static Option getActionFileOption() {
        return Option.builder("f")
                .longOpt(SONG_FILE)
                .hasArg()
                .argName("FILE")
                .desc("Path to file containing id to song mappings (required)")
                .required()
                .get();
    }

    private static Option getUrlOption() {
        return Option.builder("u")
                .longOpt(URL_OPTION)
                .hasArg()
                .argName("URL")
                .desc("URL of the Home Assistant instance (required)")
                .required()
                .get();
    }

    private static Option getEntityOption() {
        return Option.builder("e")
                .longOpt(ENTITY)
                .hasArg()
                .argName("ENTITY")
                .desc("The media player entity in Home assistant (required)")
                .required()
                .get();
    }
}
