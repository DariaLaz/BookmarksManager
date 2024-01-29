package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(7777);
        server.start();
    }
}
