package dk.simwir.musicbox.exceptions;

public class HttpStatusException extends Exception {
    private final int statusCode;

    public HttpStatusException(int statusCode) {
        super(String.format("Http Status code exception: %d", statusCode));
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
