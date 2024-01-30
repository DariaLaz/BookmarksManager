package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Request;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownCommand;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {
    private static final int BUFFER_SIZE = 10000;
    private static final String HOST = "localhost";
    private final int port;
    private boolean isRunning = true;
    private static final Gson GSON = new Gson();
    private ByteBuffer buffer;
    private Selector selector;
    private static final CommandExecutor commandExecutor = new CommandExecutor();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();

            //Configure server socket channel
            serverSocketChannel.bind(new java.net.InetSocketAddress(HOST, port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, java.nio.channels.SelectionKey.OP_ACCEPT);

            buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (isRunning) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                var selectedKeys = selector.selectedKeys();
                var keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    var key = keyIterator.next();

                    if (key.isAcceptable()) {
                        accept(selector, key);
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        String clientInput = getClientInput(clientChannel);
                        System.out.println(clientInput);
                        if (clientInput == null) {
                            continue;
                        }
                        //do to gson
                        try {
                            Response response = commandExecutor.handle(GSON.fromJson(clientInput, Request.class));
                            buffer.flip();
                            String output = GSON.toJson(response);
                            writeClientOutput(clientChannel, output);

                        } catch (UnknownCommand e) {
                            writeClientOutput(clientChannel, e.getMessage());
                            continue;
                        }
                    }

                    keyIterator.remove();
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Error occurred while starting the server: " + e.getMessage());
        }
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }
}
