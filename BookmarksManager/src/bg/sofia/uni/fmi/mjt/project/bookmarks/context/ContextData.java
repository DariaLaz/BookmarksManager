package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.file.FileManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.file.GroupsFileManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.file.UserFileManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.User;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.HttpHandler;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.chrome.BookmarksExtractor;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.chrome.GoogleChromeExtractor;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security.Hash;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security.PasswordHash;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation.Validator;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ContextData extends Context {
    private final Map<String, User> registeredUsers;
    private final Map<String, List<Group>> bookmarkGroups; // username -> groups
    private static final Hash PASSWORD_HASHER = new PasswordHash();
    private static Context instance = null;
    private static final Gson GSON = new Gson();
    private static final String ROOT = "src\\bg\\sofia\\uni\\fmi\\mjt\\project\\bookmarks\\network\\server\\data\\";
    private static final String USERS_FILE = ROOT + "users.txt";
    private static final BookmarksExtractor BOOKMARKS_EXTRACTOR = new GoogleChromeExtractor();

    private ContextData() {
        bookmarkGroups = new HashMap<>();
        registeredUsers = new HashMap<>();
    }

    public Map<String, User> cachedRegisteredUsers() {
        return registeredUsers;
    }

    public Map<String, List<Group>> cachedBookmarkGroups() {
        return bookmarkGroups;
    }

    public static Context getInstance() {
        if (instance == null) {
            instance = new ContextData();
        }
        return instance;
    }

    @Override
    public synchronized boolean isRegistered(String username) {
        //check cache
        return getUser(username) != null;
    }

    public User getUser(String username) {
        if (cachedRegisteredUsers().containsKey(username)) {
            return cachedRegisteredUsers().get(username);
        }
        UserFileManager.load(USERS_FILE, cachedRegisteredUsers(), cachedBookmarkGroups());

        if (cachedRegisteredUsers().containsKey(username)) {
            return cachedRegisteredUsers().get(username);
        }
        return null;
    }

    private void addNewBookmark(Bookmark bookmark, String path) {
        String line = GSON.toJson(bookmark) + "\n";
        GroupsFileManager.addNewBookmark(path, line);
    }

    public void addNewUser(String username, String hashedPassword)  {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateString(hashedPassword, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Password"));

        String id = generateID();
        String line = username + " " + hashedPassword + " " + id + System.lineSeparator();

        UserFileManager.addNew(USERS_FILE,
                                line,
                                getBookmarkPath(username),
                                cachedRegisteredUsers(),
                                cachedBookmarkGroups(),
                                new User(username, hashedPassword));
    }

    @Override
    public boolean isCorrectPassword(String username, String password) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateString(password, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Password"));

        if (isRegistered(username)) {
            String hashedPassword = PASSWORD_HASHER.hash(password);
            return cachedRegisteredUsers().get(username).password().equals(hashedPassword);
        }
        return false;
    }

    private static String generateID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void addGroup(String username, Group group) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        FileManager.create(getBookmarkPath(username, group.getName()));
        cachedBookmarkGroups().get(username).add(group);
    }

    private Group getGroup(String username, String groupName) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateString(groupName, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Group name"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));

        if (cachedBookmarkGroups().containsKey(username)) {
            Group gr = cachedBookmarkGroups().get(username).stream()
                    .filter(group -> group.getName().equals(groupName))
                    .findFirst().orElse(null);
            if (gr != null) {
                return gr;
            }
        }
        updateGroups(username);
        return cachedBookmarkGroups().get(username).stream()
                .filter(group -> group.getName().equals(groupName))
                .findFirst().orElse(null);
    }

    private void updateGroups(String username) {
        String path = getBookmarkPath(username);
        GroupsFileManager.updateGroups(path, cachedBookmarkGroups().get(username));
    }

    private void updateGroup(String username, String groupName) {
        String path = getBookmarkPath(username, groupName);
        GroupsFileManager.updateGroup(path, getGroup(username, groupName));
    }

    @Override
    public boolean isExistingGroup(String username, String groupName) {
        return getGroup(username, groupName) != null;
    }

    @Override
    public void addBookmark(String username, String groupName, Bookmark bookmark) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateString(groupName, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Group name"));

        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));

        Group group = getGroup(username, groupName);
        Validator.validateNotNull(group, Messages.GROUP_DOES_NOT_EXIST);

        try {
            Path path = Paths.get(getBookmarkPath(username, groupName)).toAbsolutePath();
            addNewBookmark(bookmark, path.toString());
            group.addBookmark(bookmark);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Bookmark getBookmark(String username, String groupName, String url) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateString(groupName, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Group name"));
        Validator.validateString(url, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Url"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));

        Group group = getGroup(username, groupName);

        Validator.validateNotNull(group, Messages.GROUP_DOES_NOT_EXIST);

        Bookmark bookmark = group.getBookmarks().stream().filter(b -> b.url().equals(url)).findFirst().orElse(null);

        if (bookmark != null) {
            return bookmark;
        }

        updateGroup(username, groupName);

        return group.getBookmarks().stream().filter(b -> b.url().equals(url)).findFirst().orElse(null);
    }

    @Override
    public boolean isExistingBookmark(String username, String groupName, String url) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateString(groupName, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Group name"));
        Validator.validateString(url, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Url"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));
        return getBookmark(username, groupName, url) != null;
    }

    @Override
    public void removeBookmark(String username, String groupName, String bookmarkUrl) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateString(groupName, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Group name"));
        Validator.validateString(bookmarkUrl, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Url"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));

        Group group = getGroup(username, groupName);
        Validator.validateNotNull(group, Messages.GROUP_DOES_NOT_EXIST);

        group.removeBookmark(bookmarkUrl);

        String path = getBookmarkPath(username, groupName);

        GroupsFileManager.removeBookmark(path, bookmarkUrl, group);

        group.getBookmarks().forEach(bookmark -> addNewBookmark(bookmark, path));
    }

    @Override
    public List<Bookmark> getBookmarks(String username, String groupName) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateString(groupName, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Group name"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));
        Validator.validateNotNull(getGroup(username, groupName), Messages.GROUP_DOES_NOT_EXIST);

        updateGroups(username);
        List<Bookmark> bookmarks = new ArrayList<>();

        cachedBookmarkGroups().get(username).forEach(group -> {
            if (group.getName().equals(groupName)) {
                updateGroup(username, group.getName());
                bookmarks.addAll(group.getBookmarks());
            }
        });

        return bookmarks;
    }

    @Override
    public List<Bookmark> getBookmarks(String username) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));

        List<Bookmark> bookmarks = new ArrayList<>();
        updateGroups(username);

        cachedBookmarkGroups().get(username).forEach(group -> {
            updateGroup(username, group.getName());
            bookmarks.addAll(group.getBookmarks());
        });

        return bookmarks;
    }

    @Override
    public List<Bookmark> searchByTag(String username, List<String> strings) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));
        List<Bookmark> bookmarks = getBookmarks(username);
        List<Bookmark> result = new ArrayList<>();

        bookmarks.forEach(bookmark -> {
            if (containsAnyKeyword(bookmark, strings)) {
                result.add(bookmark);
            }
        });

        return result;
    }

    private boolean containsAnyKeyword(Bookmark bookmark, List<String> keywords) {
        for (String keyword : keywords) {
            if (containsKeyword(bookmark, keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsKeyword(Bookmark bookmark, String kw) {
        return bookmark.keyWords().stream().anyMatch(keyword -> keyword.equalsIgnoreCase(kw));
    }

    @Override
    public List<Bookmark> searchByTitle(String username, String title) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateString(title, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Title"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));
        List<Bookmark> bookmarks = getBookmarks(username);
        List<Bookmark> result = new ArrayList<>();

        bookmarks.forEach(bookmark -> {
            if (bookmark.title() != null && bookmark.title().toLowerCase().contains(title.toLowerCase())) {
                result.add(bookmark);
            }
        });

        return result;
    }

    @Override
    public void cleanUp(String username) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));

        updateGroups(username);

        cachedBookmarkGroups().get(username).forEach(group -> {
            cleanUpGroup(username, group.getName());
            updateGroup(username, group.getName());
        });

    }

    @Override
    public void importFromChrome(String username) {
        Validator.validateString(username, String.format(Messages.CANNOT_BE_NULL_EMPTY, "Username"));
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));

        if (!isExistingGroup(username, "chrome")) {
            addGroup(username, new Group("chrome"));
        }
        List<Bookmark> bookmarks = BOOKMARKS_EXTRACTOR.extract();
        String path = getBookmarkPath(username, "chrome");
        GroupsFileManager.appendToFile(path, bookmarks);
    }

    private void cleanUpGroup(String username, String groupName) {
        Validator.validateRegistrate(isRegistered(username), String.format(Messages.USER_NOT_REGISTERED, username));

        updateGroup(username, groupName);
        Group group = getGroup(username, groupName);
        List<Bookmark> bookmarks = group.getBookmarks();
        List<Bookmark> result = new ArrayList<>();

        bookmarks.forEach(bookmark -> {
            if (isValidUrl(bookmark.url())) {
                result.add(bookmark);
            }
        });

        String path = getBookmarkPath(username, groupName);

        GroupsFileManager.clearFile(path);
        GroupsFileManager.appendToFile(path, result);
    }

    private String getBookmarkPath(String username, String groupName) {
        return Paths.get(ROOT + "bookmarks\\" + username + "\\" + groupName + ".txt")
                .toAbsolutePath().toString();
    }

    private String getBookmarkPath(String username) {
        return Paths.get(ROOT + "bookmarks\\" + username).toAbsolutePath().toString();
    }

    private boolean isValidUrl(String url) {
        try {
            return HttpHandler.checkIfValidUrl(url);
        } catch (IOException e) {
            return false;
        }
    }
}
