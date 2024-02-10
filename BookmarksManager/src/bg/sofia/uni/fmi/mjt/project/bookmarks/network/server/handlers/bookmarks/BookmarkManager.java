package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextBookmarks;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextData;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.SearchType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.bitly.Shortener;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.bitly.UrlSortener;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation.Validator;

import java.util.List;
import java.util.Optional;

public class BookmarkManager implements BookmarkHandler {
    private static BookmarkManager instance = null;
    protected static final ContextBookmarks CONTEXT = ContextData.getInstance();
    private static final Shortener SHORTENER = new UrlSortener();

    private BookmarkManager() {

    }

    public static BookmarkHandler getInstance() {
        if (instance == null) {
            instance = new BookmarkManager();
        }
        return instance;
    }

    public boolean addGroup(String sessionId, String groupName) throws AlreadyExistingException {
        String username = SessionManager.getUsername(sessionId);
        if (CONTEXT.isExistingGroup(username, groupName)) {
            throw new AlreadyExistingException(String.format(Messages.UNSUCCESSFUL_ADD_GROUP, groupName));
        }
        Group group = new Group(groupName);
        CONTEXT.addGroup(username, group);
        return true;
    }

    @Override
    public boolean addBookmark(String sessionId, String groupName,
                               String url, boolean isShorten) throws AlreadyExistingException {
        Validator.validateString(url, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Url"));
        Validator.validateString(groupName, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Group name"));

        String username = SessionManager.getUsername(sessionId);
        if (!CONTEXT.isExistingGroup(username, groupName)) {
            throw new IllegalArgumentException( Messages.GROUP_DOES_NOT_EXIST);
        }
        if (CONTEXT.isExistingBookmark(username, groupName, url)) {
            throw new AlreadyExistingException(String.format(Messages.URL_ALREADY_EXISTS, url));
        }

        if (isShorten) {
            url = shorten(url);
        }

        CONTEXT.addBookmark(username, groupName, getBookmark(url));

        return true;
    }

    @Override
    public boolean removeBookmark(String sessionId, String groupName, String bookmarkUrl) {
        Validator.validateString(bookmarkUrl, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Url"));
        Validator.validateString(groupName, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Group name"));

        String username = SessionManager.getUsername(sessionId);
        if (!CONTEXT.isExistingGroup(username, groupName)) {
            throw new IllegalArgumentException(Messages.GROUP_DOES_NOT_EXIST);
        }
        if (!CONTEXT.isExistingBookmark(username, groupName, bookmarkUrl)) {
            throw new IllegalArgumentException(Messages.BOOKMARK_DOES_NOT_EXIST);
        }

        CONTEXT.removeBookmark(username, groupName, bookmarkUrl);

        return true;
    }

    @Override
    public List<Bookmark> listBookmarks(String username, Optional<String> groupName) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));

        List<Bookmark> bookmarks;

        if (groupName.isPresent()) {
            bookmarks = CONTEXT.getBookmarks(username, groupName.get());
        } else {
            bookmarks = CONTEXT.getBookmarks(username);
        }
        return bookmarks;
    }

    @Override
    public List<Bookmark> search(String username, SearchType searchType, List<String> strings) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateListNotEmpty(strings, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Search strings"));

        return switch (searchType) {
            case TAG -> CONTEXT.searchByTag(username, strings);
            case TITLE -> {
                Validator.validateListSize(strings, 1, String.format(Messages.CANNOT_BE_EMPTY, "Search strings"));
                yield CONTEXT.searchByTitle(username, strings.get(0));
            }
        };
    }

    @Override
    public boolean cleanUp(String username) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));

        CONTEXT.cleanUp(username);

        return true;
    }

    @Override
    public boolean importFromChrome(String username) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));

        CONTEXT.importFromChrome(username);

        return true;
    }

    private Bookmark getBookmark(String url) {
        return Bookmark.of(url);
    }

    String shorten(String url) {
        return SHORTENER.shorten(url);
    }
}
