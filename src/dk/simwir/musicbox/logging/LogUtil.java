package dk.simwir.musicbox.logging;

import java.util.logging.Logger;

public class LogUtil {
    public static Logger getLogger(String name) {
        return Logger.getLogger("dk.simwir.musicbox." + name);
    }
}
