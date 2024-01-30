package bg.sofia.uni.fmi.mjt.project.bookmarks.models;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.Jsoup.PageExtractor;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.Jsoup.WebPageExtractor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public record Bookmark(String title,
                       String url,
                       List<String> keyWords) { // word -> count
    public static Bookmark of(String url) {
        try {
            PageExtractor webPageExtractor = new WebPageExtractor(url);
            return new Bookmark(webPageExtractor.title(), url, webPageExtractor.keywords());
        } catch (IOException e) {
            return new Bookmark(null, url, null);
        }
    }


}
