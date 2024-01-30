package bg.sofia.uni.fmi.mjt.project.bookmarks.network.client;

public class Main {
    public static void main(String[] args) {
        final int port = 7777;
        Client client = new Client(port, "localhost");
        client.start();
    }
}
