package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {
    public static boolean checkIfValidUrl(String checkUrl) throws IOException {
        URL url;
        try {
            url = new URL(checkUrl);
        } catch (Exception e) {
            return false;
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        return responseCode == HttpURLConnection.HTTP_OK;
    }
}
