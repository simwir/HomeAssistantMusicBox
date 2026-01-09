package dk.simwir.musicbox.logging;

import java.util.logging.*;

public class LogUtil {

    public static final String LOG_NAME_SPACE = "dk.simwir.musicbox";
    private static final Logger logger = Logger.getLogger(LOG_NAME_SPACE);
    private static Level level;

    private LogUtil() {}

    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(LOG_NAME_SPACE + name);
        logger.setLevel(level);
        return logger;
    }

    public static void setLevel(Level level) {
        LogUtil.level = level;
        Logger logger = Logger.getLogger(LOG_NAME_SPACE);
        logger.setLevel(level);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            if (h instanceof ConsoleHandler) {
                h.setLevel(Level.ALL);
                // Optional: choose a simple formatter
                h.setFormatter(new SimpleFormatter());
            }
        }
    }

    public static void printAllLevels() {
        LogManager.getLogManager().getLoggerNames().asIterator().forEachRemaining(log -> {
                    logger.info(() -> String.format("Log level for logger %s: %s", log, Logger.getLogger(log).getLevel()));
                    Logger parent = Logger.getLogger(log).getParent();
                    String parentName;
                    String level;
                    if (parent == null) {
                        parentName = "NULL";
                        level = "NULL";
                    } else {
                        parentName = parent.getName();
                        level = parent.getLevel().toString();
                    }
                    logger.info(String.format("Parent: %s, Level: %s", parentName, level));
                }
        );
    }
}
