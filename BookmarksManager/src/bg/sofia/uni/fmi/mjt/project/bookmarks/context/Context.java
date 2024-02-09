package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.User;

import java.util.List;
import java.util.Map;

public abstract class Context implements ContextBookmarks, ContextUsers {
    public abstract Map<String, List<Group>> cachedBookmarkGroups();

    public abstract Map<String, User> cachedRegisteredUsers();
}
