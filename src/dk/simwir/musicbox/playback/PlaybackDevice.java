package dk.simwir.musicbox.playback;

import dk.simwir.musicbox.action.Song;

public interface PlaybackDevice {
    void stop();

    void play(Song song);
}
