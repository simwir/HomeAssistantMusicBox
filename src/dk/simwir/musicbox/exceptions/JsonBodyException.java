package dk.simwir.musicbox.exceptions;

public class JsonBodyException extends RuntimeException {

    private final String body;

    public JsonBodyException(String message, Throwable cause, String body) {
        super(message, cause);
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
