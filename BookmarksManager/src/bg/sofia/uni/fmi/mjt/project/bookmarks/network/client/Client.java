package bg.sofia.uni.fmi.mjt.project.bookmarks.network.client;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Request;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownUser;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private final int port;
    private final String host;
    private static final int BUFFER_SIZE = 10000;
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private String sessionId = null;
    private static final Gson GSON = new Gson();
    private boolean isRunning = true;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    private void setSessionId(String sessionId) {
        if (sessionId == null) {
            throw new UnknownUser("Session id is null");
        }
        this.sessionId = sessionId;
    }

    private void removeSession() {
        this.sessionId = null;
    }

    private void handleResponse(Response response) {
        if (response.commandType().equals(CommandType.LOGIN)) {
            setSessionId(response.sessionID());
        } else if (response.commandType().equals(CommandType.LOGOUT)) {
            removeSession();
        }
    }

    private void handleReply(String reply) {
        Response response = GSON.fromJson(reply, Response.class);
        handleResponse(response);
        System.out.println(response.message());
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
            Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(host, port));

            while (isRunning) {
                System.out.print("Enter command: ");
                String message = scanner.nextLine();

                Request request = new Request(message, sessionId);
                buffer.clear();
                buffer.put(GSON.toJson(request).getBytes());
                buffer.flip();
                socketChannel.write(buffer);

                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();

                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String reply = new String(byteArray, StandardCharsets.UTF_8); // buffer drain

                handleReply(reply);
            }

        } catch (IOException e) {
            throw new UnknownUser(e.getMessage());
        }
    }
}
