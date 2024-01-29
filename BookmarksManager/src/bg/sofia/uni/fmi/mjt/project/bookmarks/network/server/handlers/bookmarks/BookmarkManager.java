package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextBookmarks;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextData;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation.Validator;

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

    public boolean addGroup(String sessionId, String groupName) throws AlreadyExistingException {
        String username = SessionManager.getUsername(sessionId);
        if (context.isExistingGroup(username, groupName)) {
            throw new AlreadyExistingException("Cannot add group " + groupName + " because it already exists");
        }
        Group group = new Group(groupName);
        context.addGroup(username, group);
        return true;
    }

    @Override
    public boolean addBookmark(String sessionId, String groupName,
                               String url, boolean isShorten) throws AlreadyExistingException {

        Validator.validateString(url, "Url cannot be null or empty");
        Validator.validateString(groupName, "Group name cannot be null or empty");

        String username = SessionManager.getUsername(sessionId);
        if (!context.isExistingGroup(username, groupName)) {
            throw new IllegalArgumentException("Cannot add bookmark to group " + groupName + " because it does not exist");
        }
        if (context.isExistingBookmark(username, groupName, url)) {
            throw new AlreadyExistingException("Cannot add bookmark " + url + " because it already exists");
        }

        if(isShorten) {
            url = shorten(url);
        }

        context.addBookmark(username, groupName, getBookmark(url));

        return true;
    }

    @Override
    public boolean removeBookmark(String sessionId, String groupName, String bookmarkUrl) {
        Validator.validateString(bookmarkUrl, "Url cannot be null or empty");
        Validator.validateString(groupName, "Group name cannot be null or empty");

        String username = SessionManager.getUsername(sessionId);
        if (!context.isExistingGroup(username, groupName)) {
            throw new IllegalArgumentException("Cannot remove bookmark from group " + groupName + " because it does not exist");
        }
        if (!context.isExistingBookmark(username, groupName, bookmarkUrl)) {
            throw new IllegalArgumentException("Cannot remove bookmark " + bookmarkUrl + " because it does not exist");
        }

        context.removeBookmark(username, groupName, bookmarkUrl);

        return true;
    }

    private Bookmark getBookmark(String url){
        //ToDo implement
        return Bookmark.of(url);
    }

    String shorten(String url) {
        //ToDo implement
        return url;
    }
}
