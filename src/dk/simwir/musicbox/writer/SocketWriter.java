package dk.simwir.musicbox.writer;

import dk.simwir.musicbox.logging.LogUtil;
import dk.simwir.musicbox.reader.Id;
import dk.simwir.musicbox.reader.IdReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketWriter extends Thread implements AutoCloseable{
    private final IdReader idReader;
    private boolean running = false;
    private final InetAddress inetAddress;
    private final int port;
    private static final Logger logger = LogUtil.getLogger("writer.SocketWriter");

    public SocketWriter(IdReader idReader, InetAddress inetAddress, int port) {
        this.idReader = idReader;
        this.inetAddress = inetAddress;
        this.port = port;
    }

    @Override
    public void run() {
        running = true;
        try (Socket socket = new Socket(inetAddress, port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (running && !socket.isClosed() && !Thread.interrupted()) {
                Id message = idReader.read();
                out.println(message.id());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unhandled IOException in SocketWriter", e);
            Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
           logger.log(Level.WARNING, "SocketWriter exiting due to InterruptedException", e);
           Thread.currentThread().interrupt();
        } finally {
            logger.info("Connection closed");
        }
    }

    @Override
    public void close() {
        running = false;
        this.interrupt();
    }
}
