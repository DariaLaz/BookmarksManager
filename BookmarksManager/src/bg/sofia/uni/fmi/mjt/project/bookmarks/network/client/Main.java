package bg.sofia.uni.fmi.mjt.project.bookmarks.network.client;

import com.github.shyiko.dotenv.DotEnv;

public class Main {
    public static void main(String[] args) {
        final int port = Integer.parseInt(DotEnv.load().get("PORT"));

        Client client = new Client(port, "localhost");
        client.start();
    }
}
