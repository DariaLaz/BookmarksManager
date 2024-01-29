package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextBookmarks;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextUsers;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextData;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation.Validator;

import javax.naming.AuthenticationException;
import java.util.List;

public class BookmarkManager implements BookmarkHandler {
    private static BookmarkManager INSTANCE = null;
    protected static final ContextBookmarks context = ContextData.getInstance();
    private static final Validator VALIDATOR = new Validator();

    private BookmarkManager() {

    }
    public static BookmarkHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BookmarkManager();
        }
        return INSTANCE;
    }

    public boolean addGroup(String username, String groupName) throws AlreadyExistingException {
        if (context.isExistingGroup(username, groupName)) {
            throw new AlreadyExistingException("Cannot add group " + groupName + " because it already exists");
        }

        Group group = new Group(groupName);
        context.addGroup(username, group);
        return true;
    }
    public boolean addBookmark(String username, String groupName, String bookmarkName, String url) {
        return false;
    }

}
