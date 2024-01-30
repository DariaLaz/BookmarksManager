package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.chrome;

import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class GoogleChromeExtractor implements BookmarksExtractor {
    private static final String BOOKMARKS_FILE_PATH =
            "C:\\Users\\User\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Bookmarks";
    private static final Gson GSON = new Gson();
    @Override
    public List<Bookmark> extract() {
        List<Bookmark> bookmarks = new ArrayList<>();
        String bookmarksFilePath = "C:\\Users\\Dari\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Bookmarks";

        JsonElement bookmarksJson = parseBookmarksFile(bookmarksFilePath);

        if (bookmarksJson == null) {
            return bookmarks;
        }

        JsonObject rootObject = bookmarksJson.getAsJsonObject();
        JsonObject roots = rootObject.getAsJsonObject("roots");
        JsonObject bookmarkBar = roots.getAsJsonObject("bookmark_bar");
        JsonArray children = bookmarkBar.getAsJsonArray("children");

        if (children != null) {
            for (JsonElement root : children) {
                String url = root.getAsJsonObject().get("url").getAsString();
                bookmarks.add(Bookmark.of(url));
            }
        }
        return bookmarks;
    }

    private static JsonElement parseBookmarksFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JsonParser parser = new JsonParser();
            return parser.parse(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error reading or parsing the bookmarks file: ", e);
        }
    }
}
