package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.Logger;
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
import java.util.Iterator;

public class Server {
    private static final int BUFFER_SIZE = 16384;
    private static final String HOST = "localhost";
    private final int port;
    private boolean isRunning = true;
    private static final Gson GSON = new Gson();
    private ByteBuffer buffer;
    private Selector selector;
    private static final CommandExecutor COMMAND_EXECUTOR = new CommandExecutor();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel);
            buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (isRunning) {
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;
                }

                var selectedKeys = selector.selectedKeys();
                var keyIterator = selectedKeys.iterator();

                handleKeys(keyIterator);
            }
        } catch (IOException e) {
            Logger.getInstance().log(e);
            System.out.println("There was a problem with the server");
        }
    }

    private void handleKeys(Iterator<SelectionKey> keyIterator) throws IOException {
        while (keyIterator.hasNext()) {
            var key = keyIterator.next();
            if (key.isAcceptable()) {
                accept(selector, key);
            } else if (key.isReadable()) {
                SocketChannel clientChannel = (SocketChannel) key.channel();

                String clientInput = getClientInput(clientChannel);

                if (clientInput == null || !handleClientInput(clientChannel, clientInput)) {
                    continue;
                }
            }
            keyIterator.remove();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel serverSocketChannel) throws IOException {
        serverSocketChannel.bind(new java.net.InetSocketAddress(HOST, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, java.nio.channels.SelectionKey.OP_ACCEPT);
    }

    private boolean handleClientInput(SocketChannel clientChannel, String clientInput) throws IOException {
        try {
            Response response = COMMAND_EXECUTOR.handle(GSON.fromJson(clientInput, Request.class));
            buffer.flip();
            String output = GSON.toJson(response);
            writeClientOutput(clientChannel, output);
            return true;
        } catch (UnknownCommand e) {
            writeClientOutput(clientChannel, e.getMessage());
            Logger.getInstance().log(e);
            return false;
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
