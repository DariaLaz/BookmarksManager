package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;

public interface BookmarkHandler {
    boolean addGroup(String username, String groupName) throws AlreadyExistingException;
}

