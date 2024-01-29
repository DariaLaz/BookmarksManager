package bg.sofia.uni.fmi.mjt.project.bookmarks.models;

import java.util.Map;

public record Bookmark(String title,
                       String url,
                       Map<String, Integer> keyWords) { // word -> count
    public Bookmark of(String url) {
        return new Bookmark(getTitle(url), url, getKeyWords(url));
    }

    private Map<String, Integer> getKeyWords(String url) {
        //todo implement
        return null;
    }

    private String getTitle(String url) {
        //todo implement
        return null;
    }
}
