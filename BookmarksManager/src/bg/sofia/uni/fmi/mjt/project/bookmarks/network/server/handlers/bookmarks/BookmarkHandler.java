package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;

public interface BookmarkHandler {
    boolean addGroup(String sessionId, String groupName) throws AlreadyExistingException;
    boolean addBookmark(String sessionId, String groupName, String url, boolean isShorten) throws AlreadyExistingException;

    boolean removeBookmark(String sessionId, String groupName, String bookmarkUrl);
}

