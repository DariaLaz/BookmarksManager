package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.SearchType;

import java.util.List;
import java.util.Optional;

public interface BookmarkHandler {
    boolean addGroup(String sessionId, String groupName) throws AlreadyExistingException;

    boolean addBookmark(String sessionId, String groupName, String url, boolean isShorten)
            throws AlreadyExistingException;

    boolean removeBookmark(String sessionId, String groupName, String bookmarkUrl);

    List<Bookmark> listBookmarks(String username, Optional<String> groupName);

    List<Bookmark> search(String username, SearchType searchType, List<String> strings);

    boolean cleanUp(String username);

    boolean importFromChrome(String username);
}

