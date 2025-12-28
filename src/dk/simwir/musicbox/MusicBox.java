package dk.simwir.musicbox;

import dk.simwir.musicbox.action.Action;
import dk.simwir.musicbox.exceptions.PlaybackException;
import dk.simwir.musicbox.logging.LogUtil;
import dk.simwir.musicbox.reader.IdReader;
import dk.simwir.musicbox.action.ActionService;
import dk.simwir.musicbox.playback.PlaybackService;
import dk.simwir.musicbox.reader.Id;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MusicBox implements Runnable {

    private final IdReader idReader;
    private final ActionService actionService;
    private final PlaybackService playbackService;

    private static final Logger logger = LogUtil.getLogger("");

    public MusicBox(IdReader idReader, ActionService actionService, PlaybackService playbackService) {
        this.idReader = idReader;
        this.actionService = actionService;
        this.playbackService = playbackService;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Id id = idReader.read();
                Action action = actionService.getAction(id);
                playbackService.execute(action);
            } catch (PlaybackException e) {
                logger.log(Level.SEVERE, e, () -> "Unhandled exception in MusicBox. Terminating thread with wrapped in Runtime exception");
                throw new RuntimeException(e);
            }
        }
    }
}