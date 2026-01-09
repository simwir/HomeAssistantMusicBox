package dk.simwir.musicbox.playback;

import dk.simwir.musicbox.action.Song;
import dk.simwir.musicbox.exceptions.PlaybackException;

public interface PlaybackDevice {
    void stop() throws PlaybackException, InterruptedException;

    void play(Song song) throws PlaybackException, InterruptedException;
}
