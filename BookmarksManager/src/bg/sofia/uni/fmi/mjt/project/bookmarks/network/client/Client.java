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
import java.util.Scanner;

public class Client {
    private final int port;
    private final String host;
    private static final int BUFFER_SIZE = 512;
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private String sessionId = null;
    private static final Gson GSON = new Gson();

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
        }
        else if (response.commandType().equals(CommandType.LOGOUT)) {
            removeSession();
        }
    }

    public void start() {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(host, port));

            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine(); // read a line from the console

                if ("quit".equals(message)) {
                    break;
                }

//                System.out.println("Sending message <" + message + "> to the server...");
                Request request = new Request(message, sessionId);
                buffer.clear(); // switch to writing mode
                buffer.put(GSON.toJson(request).getBytes()); // buffer fill
                buffer.flip(); // switch to reading mode
                socketChannel.write(buffer); // buffer drain

                buffer.clear(); // switch to writing mode
                socketChannel.read(buffer); // buffer fill
                buffer.flip(); // switch to reading mode

                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String reply = new String(byteArray, "UTF-8"); // buffer drain
                Response response = GSON.fromJson(reply, Response.class);
                handleResponse(response);
                System.out.println(response.message());
            }

        } catch (IOException e) {
            throw new UnknownUser(e.getMessage());
        }
    }
}
