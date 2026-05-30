package dk.simwir.musicbox.playback;

import dk.simwir.musicbox.action.Action;
import dk.simwir.musicbox.exceptions.PlaybackException;

public interface PlaybackService {
    void execute(Action action) throws PlaybackException, InterruptedException;
}
