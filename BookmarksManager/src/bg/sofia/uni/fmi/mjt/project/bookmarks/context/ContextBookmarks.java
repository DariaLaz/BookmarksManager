package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;

import javax.naming.AuthenticationException;
import java.util.List;

public interface ContextBookmarks {
    List<Group> getBookmarkGroups(String username);
    void addGroup(String username, Group group);
    boolean isExistingGroup(String username, String groupName);
    void addBookmark(String username, String groupName, Bookmark bookmark);
    boolean isExistingBookmark(String username, String groupName, String url);
}
