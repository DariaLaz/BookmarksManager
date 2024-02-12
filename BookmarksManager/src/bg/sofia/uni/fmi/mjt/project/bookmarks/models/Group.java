package bg.sofia.uni.fmi.mjt.project.bookmarks.models;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<Bookmark> bookmarks;

    public Group(String name) {
        this.name = name;
        bookmarks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void addBookmark(Bookmark bookmark) {
        bookmarks.add(bookmark);
    }

    public void removeBookmark(String bookmarkUrl) {
        bookmarks.removeIf(bookmark -> bookmark.url().equals(bookmarkUrl));
    }

    public void removeAllBookmarks() {
        bookmarks.clear();
    }
}
