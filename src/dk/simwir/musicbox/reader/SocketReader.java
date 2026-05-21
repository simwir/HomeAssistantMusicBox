package dk.simwir.musicbox.reader;

import dk.simwir.musicbox.exceptions.SocketReaderException;
import dk.simwir.musicbox.logging.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketReader extends Thread implements IdReader, AutoCloseable {
    private final DataTransfer<Id> dataTransfer = new DataTransfer<>();
    private final List<WeakReference<SocketConnection>> socketConnections = new ArrayList<>();
    private final int port;
    private boolean running;
    private final Monitor<Boolean> readyMonitor = new Monitor<>(Boolean.FALSE);
    private static final Logger logger = LogUtil.getLogger("reader.SocketReader");

    public SocketReader(int port) {
        this.port = port;
    }

    @Override
    public Id read() throws InterruptedException {
        return dataTransfer.get();
    }

    @Override
    public void run() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info(() -> String.format("Ready to accept connections on port %s", port));
            readyMonitor.set(Boolean.TRUE);
            while (running && !Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                logger.info(() -> String.format("Connection accepted from %s", socket.getInetAddress()));
                SocketConnection socketConnection = new SocketConnection(socket);
                socketConnections.add(new WeakReference<>(socketConnection));
                socketConnection.start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unhandled IOException in SocketReader", e);
            Thread.currentThread().interrupt();
        } finally {
            readyMonitor.set(Boolean.FALSE);
            socketConnections.stream().map(Reference::get).filter(Objects::nonNull).forEach(SocketConnection::close);
            running = false;
        }
    }

    @Override
    public void close() {
        running = false;
        this.interrupt();
    }

    public void waitForReady() throws InterruptedException {
        logger.info("Waiting for SocketReader ready");
        readyMonitor.waitFor(Boolean.TRUE);
        logger.info("SocketReader ready");
    }

    private class SocketConnection extends Thread implements AutoCloseable{

        private final Socket socket;
        private boolean running;

        private SocketConnection(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            running = true;
            try (Socket localSocket = this.socket;
                 BufferedReader in = new BufferedReader(new InputStreamReader(localSocket.getInputStream()))) {
                logger.info(() -> String.format("Connection to %s, ready to read", localSocket.getInetAddress()));
                while (running && !localSocket.isClosed() && !Thread.interrupted()) {
                    String input = in.readLine();
                    logger.finer(() -> String.format("Read %s from socket %s", input, localSocket.getInetAddress()));
                    dataTransfer.set(new Id(input));
                }
            }catch (IOException e) {
                throw new SocketReaderException(e);
            } finally {
                logger.info(() -> String.format("Connection to %s closed", socket.getInetAddress()));
                running = false;
            }
        }

        @Override
        public void close() {
            running = false;
            this.interrupt();
        }

        private static String readUntilNull(BufferedReader in) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            int ch;
            while ((ch = in.read()) != -1) {
                if (ch == '\0') break;
                stringBuilder.append((char) ch);
            }
            return stringBuilder.toString();
        }
    }
}
