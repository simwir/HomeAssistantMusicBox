package dk.simwir.musicbox.playback;

import dk.simwir.musicbox.action.Song;
import dk.simwir.musicbox.logging.LogUtil;

import java.util.logging.Logger;

public class LoggerPlaybackDevice implements PlaybackDevice {
    private static final Logger logger = LogUtil.getLogger("playback.LoggerPlaybackDevice");
    @Override
    public void stop() {
        logger.info(() -> "Stopping Playback");
    }

    @Override
    public void play(Song song) {
        logger.info(() -> String.format("Playing: %s", song.key()));
    }
}
