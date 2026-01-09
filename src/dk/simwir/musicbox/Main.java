import dk.simwir.musicbox.ArgumentParser;
import dk.simwir.musicbox.MusicBox;
import dk.simwir.musicbox.action.ActionFactory;
import dk.simwir.musicbox.homeassistant.HomeAssistantClientImpl;
import dk.simwir.musicbox.logging.LogUtil;
import dk.simwir.musicbox.playback.HomeAssistantPlaybackDevice;
import dk.simwir.musicbox.playback.PlaybackServiceImpl;
import dk.simwir.musicbox.reader.StandardReader;
import org.apache.commons.cli.ParseException;

import java.net.http.HttpClient;
import java.util.logging.Level;
import java.util.logging.Logger;

private final List<Instant> retries = new ArrayList<>();
public static final Duration RETRY_WINDOW = Duration.ofMinutes(10);
public static final int RETRY_COUNT = 3;
private static final Logger logger = LogUtil.getLogger("Main");

void main(String[] args) throws IOException, ParseException, InterruptedException {
    LogUtil.setLevel(Level.FINEST);
    ArgumentParser.Arguments arguments = ArgumentParser.parseArgs(args);
    while (!isRetryExceeded()) {
        MusicBox musicBox = getMusicBox(arguments);
        Thread thread = new Thread(musicBox);
        logger.info("Starting new MusicBox thread");
        thread.start();
        thread.join();
        if (musicBox.getUncaughtException() != null) {
            logger.log(Level.WARNING, "MusicBox existed with uncaught exception. Attempting restart");
            retries.add(Instant.now());
        } else {
            logger.info("Music box exited normally. Not restarting");
            break;
        }
    }
}

private boolean isRetryExceeded() {
    Instant earliestTime = Instant.now().minus(RETRY_WINDOW);
    long numRetries = retries.stream().filter(x -> x.isAfter(earliestTime)).count();
    logger.info(() -> String.format("%d retries in the last %s minutes. Limit %d", numRetries, RETRY_WINDOW, RETRY_COUNT));
    return numRetries > RETRY_COUNT;
}

private static MusicBox getMusicBox(ArgumentParser.Arguments arguments) throws IOException {
    return new MusicBox(
            new StandardReader(),
            ActionFactory.getActionServiceFromFile(arguments.actionFile()),
            new PlaybackServiceImpl(new HomeAssistantPlaybackDevice(
                    new HomeAssistantClientImpl(
                            HttpClient.newHttpClient(),
                            arguments.url(),
                            arguments.haToken(),
                            Duration.ofSeconds(10)
                    ),
                    arguments.entity()
            ))
    );
}
