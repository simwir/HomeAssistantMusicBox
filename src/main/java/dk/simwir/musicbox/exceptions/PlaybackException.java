package dk.simwir.musicbox.exceptions;

public class PlaybackException extends Exception {

    public PlaybackException() {
        super();
    }

    public PlaybackException(String message) {
        super(message);
    }

    public PlaybackException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlaybackException(Throwable cause) {
        super(cause);
    }
}
