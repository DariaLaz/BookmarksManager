package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.bitly;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.ShortenException;
import com.github.shyiko.dotenv.DotEnv;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UrlSortener implements Shortener {
    private static final String BITLY_TOKEN = DotEnv.load().get("BITLY_TOKEN");
    private static final String ROOT_BITLY = "https://api-ssl.bitly.com/v4/shorten";
    private static final String LONGURL = "long_url";
    @Override
    public String shorten(String url) {
        String body = String.format("{ \"%s\": \"%s\" }", LONGURL, url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_BITLY))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", BITLY_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response;
        var client = HttpClient.newHttpClient();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException("Cannot create shortened url.");
        }

        switch (response.statusCode()) {
            case HttpURLConnection.HTTP_OK -> {
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                return jsonObject.get("link").getAsString();
            }

            default -> {
                throw new ShortenException("Cannot create shortened url.");
            }
        }
    }
}
