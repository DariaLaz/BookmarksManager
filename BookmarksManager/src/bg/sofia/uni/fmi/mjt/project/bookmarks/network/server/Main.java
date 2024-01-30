package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server;

import com.github.shyiko.dotenv.DotEnv;

public class Main {
    public static void main(String[] args) {
        final int port = Integer.parseInt(DotEnv.load().get("PORT"));
        Server server = new Server(port);
        server.start();
    }
}
