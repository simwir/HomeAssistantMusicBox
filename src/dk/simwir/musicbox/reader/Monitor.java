package dk.simwir.musicbox.reader;

public class Monitor<T> {
    private T data;
    private final Object mutex = new Object();

    public Monitor(T data) {
        this.data = data;
    }

    public synchronized void waitFor(T value) throws InterruptedException {
        T currentValue;
        synchronized (mutex) {
            currentValue = data;
        }
        while (!value.equals(currentValue)) {
            wait();
            synchronized (mutex) {
                currentValue = data;
            }
        }
    }

    public synchronized void set(T value) {
        synchronized (mutex) {
            data = value;
        }
        notifyAll();
    }
}
