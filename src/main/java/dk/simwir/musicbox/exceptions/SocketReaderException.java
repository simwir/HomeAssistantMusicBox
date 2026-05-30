package dk.simwir.musicbox.exceptions;

public class SocketReaderException extends RuntimeException {
    public SocketReaderException(String message) {
        super(message);
    }

    public SocketReaderException(Throwable cause) {
        super(cause);
    }
}
