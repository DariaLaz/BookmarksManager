package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server;

public class Main {
    public static void main(String[] args) {
        final int port = Integer.parseInt(System.getenv("PORT"));
        Server server = new Server(port);
        server.start();
    }
}
