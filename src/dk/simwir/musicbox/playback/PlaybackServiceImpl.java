package dk.simwir.musicbox.playback;

import dk.simwir.musicbox.action.Action;
import dk.simwir.musicbox.action.PlayAction;
import dk.simwir.musicbox.action.StopAction;
import dk.simwir.musicbox.exceptions.PlaybackException;

public class PlaybackServiceImpl implements PlaybackService {

    private final PlaybackDevice playbackDevice;

    public PlaybackServiceImpl(PlaybackDevice playbackDevice) {
        this.playbackDevice = playbackDevice;
    }

    @Override
    public void execute(Action action) throws PlaybackException, InterruptedException {
        if (action instanceof StopAction) {
            playbackDevice.stop();
        } else if (action instanceof PlayAction playAction) {
            playbackDevice.play(playAction.getSong());
        } else {
            throw new PlaybackException(String.format("Unknown Action %s", action.getClass().getSimpleName()));
        }
    }
}
