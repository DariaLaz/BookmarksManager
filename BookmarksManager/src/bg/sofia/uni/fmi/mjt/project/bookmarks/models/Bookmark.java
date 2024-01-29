package bg.sofia.uni.fmi.mjt.project.bookmarks.models;

import java.util.Map;

public record Bookmark(String title,
                       String url,
                       Map<String, Integer> keyWords) { // word -> count
    public static Bookmark of(String url) {
        return new Bookmark(getTitle(url), url, getKeyWords(url));
    }

    private static Map<String, Integer> getKeyWords(String url) {
        //todo implement
        return Map.of("fmi", 1,
                      "sofia", 2,
                      "sliven", 3
        );
    }

    private static String getTitle(String url) {
        //todo implement
        return "title";
    }
}
