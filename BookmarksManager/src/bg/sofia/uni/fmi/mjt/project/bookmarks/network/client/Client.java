package bg.sofia.uni.fmi.mjt.project.bookmarks.network.client;

import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Request;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownUser;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    private String handleResponse(Response response) {
        if (!response.successful()) {
            return response.message();
        }

        switch (response.commandType()) {
            case LOGIN: {
                setSessionId(response.sessionID());
                return response.message();
            }
            case LOGOUT: {
                removeSession();
                return response.message();
            }
            case SEARCH, LIST: {
                List<Bookmark> bookmarks = Arrays.stream(GSON.fromJson(response.message(), Bookmark[].class)).toList();
                return listResultToString(bookmarks);
            }
            default: return response.message();
        }
    }

    private String listResultToString(List<Bookmark> bookmarks) {
        StringBuilder bookmarksString = new StringBuilder();
        for (Bookmark bookmark : bookmarks) {
            if (Objects.isNull(bookmark.title()) || bookmark.title().isBlank()) {
                bookmarksString.append(bookmark.url()).append("\n");
                continue;
            }
            bookmarksString.append(bookmark.title()).append(" -> ").append(bookmark.url()).append("\n");
        }
        return bookmarksString.toString();
    }

    private void handleReply(String reply) {
        Response response = GSON.fromJson(reply, Response.class);
        System.out.println(handleResponse(response));
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
