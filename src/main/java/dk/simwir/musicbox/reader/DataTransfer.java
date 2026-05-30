package dk.simwir.musicbox.reader;

public class DataTransfer<T> {
    private T data = null;
    private final Object mutex = new Object();

    public synchronized T get() throws InterruptedException {
        T value;
        synchronized (mutex) {
            value = data;
        }
        while (value == null) {
            wait();
            synchronized (mutex) {
                value = data;
                if (value != null) {
                    data = null;
                }
            }
        }
        return value;
    }

    public synchronized void set(T value) {
        synchronized (mutex) {
            data = value;
        }
        notifyAll();
    }
}
