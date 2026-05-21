package dk.simwir.musicbox.exceptions;

public class SocketWriterException extends RuntimeException {
    public SocketWriterException(String message) {
        super(message);
    }

    public SocketWriterException(Throwable cause) {
        super(cause);
    }
}
