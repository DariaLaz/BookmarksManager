package bg.sofia.uni.fmi.mjt.project.bookmarks.network.client;

public class Main {
    public static void main(String[] args) {
        Client client = new Client(7777, "localhost");
        client.start();
    }
}
