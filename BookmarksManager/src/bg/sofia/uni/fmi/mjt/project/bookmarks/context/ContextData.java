package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AuthException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.User;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.HttpHandler;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.chrome.BookmarksExtractor;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.chrome.GoogleChromeExtractor;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security.Hash;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security.PasswordHash;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation.Validator;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ContextData extends Context {
    private Map<String, User> registeredUsers;
    private Map<String, List<Group>> bookmarkGroups; // username -> groups
    private static final Hash passwordHasher = new PasswordHash();
    private static Context INSTANCE = null;
    private static final Gson GSON = new Gson();
    private static final String ROOT = "D:\\github\\BookmarksManager\\BookmarksManager\\src\\bg\\sofia\\uni\\fmi\\mjt\\project\\bookmarks\\network\\server\\data\\";
    private static final String USERS_FILE = ROOT + "users.txt";
    private static final BookmarksExtractor BOOKMARKS_EXTRACTOR = new GoogleChromeExtractor();


    private ContextData() {
        bookmarkGroups = new HashMap<>();
        registeredUsers = new HashMap<>();
    }

    public static final Context getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContextData();
        }
        return INSTANCE;
    }

    public Map<String, User> getRegisteredUsers(){
        return registeredUsers;
    }
    @Override
    public synchronized boolean isRegistered(String username) {
        //check cache
        return getUser(username) != null;
    }
    public User getUser(String username){
        if (getRegisteredUsers().containsKey(username)){
            return getRegisteredUsers().get(username);
        }
        try (Reader reader = new FileReader(USERS_FILE)) {
            load(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (getRegisteredUsers().containsKey(username)){
            return getRegisteredUsers().get(username);
        }
        return null;
    }
    private void setToRegisteredUsers(String username, User user){
        getRegisteredUsers().put(username, user);
    }

    private void addNewBookmark(Bookmark bookmark, String path){
        String line = GSON.toJson(bookmark) + "\n";
        try (Writer writer = new StringWriter()) {
            writer.write(line);
            Files.write(Paths.get(path), writer.toString().getBytes(), java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewUser(String username, String hashedPassword)  {
        String id = generateID();
        String line = username + " " + hashedPassword + " " + id + "\n";
        try (Writer writer = new StringWriter()) {
            writer.write(line);
            Files.write(Paths.get(USERS_FILE), writer.toString().getBytes(), java.nio.file.StandardOpenOption.APPEND);
            Files.createDirectories(Paths.get(ROOT + "bookmarks\\" + username));
            registeredUsers.put(username, new User(username, hashedPassword));
            bookmarkGroups.put(username, new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean isCorrectPassword(String username, String password) {
        Validator.validateString(username, "Username cannot be null or empty");
        Validator.validateString(password, "Password cannot be null or empty");


        if (isRegistered(username)) {
            String hashedPassword = passwordHasher.hash(password);
            return getRegisteredUsers().get(username).password().equals(hashedPassword);
        }
        return false;
    }
    private static String generateID(){
        return UUID.randomUUID().toString();
    }
    @Override
    public void load(Reader reader) {

        try (var r = new BufferedReader(reader);) {

            String line;
            while ((line = r.readLine()) != null && !line.isBlank()) {
                String[] tokens = line.split(" ");
                String username = tokens[0];
                String hashedPassword = tokens[1];
                String id = tokens[2];
                setToRegisteredUsers(username, new User(username, hashedPassword, id));
                bookmarkGroups.put(username, new ArrayList<>());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addGroup(String username, Group group) {
        Validator.validateString(username, "Username cannot be null or empty");
        try {
            Files.createFile(Paths.get(ROOT + "bookmarks\\" + username + "\\" + group.getName() + ".txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bookmarkGroups.get(username).add(group);
    }

    private Group getGroup(String username, String groupName){
        Validator.validateString(username, "Username cannot be null or empty");
        Validator.validateString(groupName, "Group name cannot be null or empty");

        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }

        if(bookmarkGroups.containsKey(username)){
            Group gr = bookmarkGroups.get(username).stream().filter(group -> group.getName().equals(groupName)).findFirst().orElse(null);
            if (gr != null) {
                return gr;
            }
        };
        updateGroups(username);
        return bookmarkGroups.get(username).stream().filter(group -> group.getName().equals(groupName)).findFirst().orElse(null);
    }

    private void updateGroups(String username){
        String path = ROOT + "bookmarks\\" + username;
        try {
            Files.walk(Paths.get(path)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String fileName = filePath.getFileName().toString().replace(".txt", "");
                    if (bookmarkGroups.get(username).stream().noneMatch(group -> group.getName().equals(fileName))) {
                        bookmarkGroups.get(username).add(new Group(fileName));
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateGroup(String username, String groupName){
        String path = ROOT + "bookmarks\\" + username + "\\" + groupName + ".txt";
        String line;
        try (Reader reader = new FileReader(path);
             var r = new BufferedReader(reader);) {
            while ((line = r.readLine()) != null && !line.isBlank()) {
                Bookmark b = GSON.fromJson(line, Bookmark.class);
                if (getGroup(username, groupName).getBookmarks().stream().noneMatch(book -> book.url().equals(b.url()))) {
                    getGroup(username, groupName).addBookmark(b);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean isExistingGroup(String username, String groupName) {
        return getGroup(username, groupName) != null;
    }

    @Override
    public void addBookmark(String username, String groupName, Bookmark bookmark) {
        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }

        Group group = getGroup(username, groupName);
        if (group == null) {
            throw new IllegalArgumentException("Group " + groupName + " does not exist");
        }

        try {
            addNewBookmark(bookmark, ROOT + "bookmarks\\" + username + "\\" + groupName + ".txt");
            group.addBookmark(bookmark);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Bookmark getBookmark(String username, String groupName, String url){
        Validator.validateString(username, "Username cannot be null or empty");
        Validator.validateString(groupName, "Group name cannot be null or empty");
        Validator.validateString(url, "Url cannot be null or empty");

        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }

        Group group = getGroup(username, groupName);

        if (group == null) {
            throw new IllegalArgumentException("Group " + groupName + " does not exist");
        }

        Bookmark bookmark = group.getBookmarks().stream().filter(b -> b.url().equals(url)).findFirst().orElse(null);

        if (bookmark != null) {
            return bookmark;
        }

        updateGroup(username, groupName);

        return group.getBookmarks().stream().filter(b -> b.url().equals(url)).findFirst().orElse(null);
    }

    @Override
    public boolean isExistingBookmark(String username, String groupName, String url) {
        return getBookmark(username, groupName, url) != null;
    }

    @Override
    public void removeBookmark(String username, String groupName, String bookmarkUrl) {
        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }

        Group group = getGroup(username, groupName);

        if (group == null) {
            throw new IllegalArgumentException("Group " + groupName + " does not exist");
        }

        group.removeBookmark(bookmarkUrl);

        String path = ROOT + "bookmarks\\" + username + "\\" + groupName + ".txt";
        try (Writer writer = new StringWriter()) {
            Files.write(Paths.get(path), writer.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        group.getBookmarks().forEach(bookmark -> addNewBookmark(bookmark, path));
    }

    @Override
    public List<Bookmark> getBookmarks(String username, String s) {

        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }
        if (!isExistingGroup(username, s)) {
            throw new IllegalArgumentException("Group " + s + " does not exist");
        }
        updateGroups(username);

        List<Bookmark> bookmarks = new ArrayList<>();

        bookmarkGroups.get(username).forEach(group -> {
            if(group.getName().equals(s)) {
                updateGroup(username, group.getName());
                bookmarks.addAll(group.getBookmarks());
            }
        });

        return bookmarks;
    }

    @Override
    public List<Bookmark> getBookmarks(String username) {
        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }

        List<Bookmark> bookmarks = new ArrayList<>();
        updateGroups(username);

        bookmarkGroups.get(username).forEach(group -> {
            updateGroup(username, group.getName());
            bookmarks.addAll(group.getBookmarks());
        });

        return bookmarks;
    }

    @Override
    public List<Bookmark> searchByTag(String username, List<String> strings) {
        List<Bookmark> bookmarks = getBookmarks(username);
        List<Bookmark> result = new ArrayList<>();

        bookmarks.forEach(bookmark -> {
            if (containsAnyKeyword(bookmark, strings)) {
                result.add(bookmark);
            }
        });

        return result;
    }
    private boolean containsAnyKeyword(Bookmark bookmark, List<String> keywords){
        for (String keyword : keywords) {
            if (containsKeyword(bookmark, keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsKeyword(Bookmark bookmark, String kw) {
        return bookmark.keyWords().stream().anyMatch(keyword -> keyword.equals(kw));
    }


    @Override
    public List<Bookmark> searchByTitle(String username, String s) {
        List<Bookmark> bookmarks = getBookmarks(username);
        List<Bookmark> result = new ArrayList<>();

        bookmarks.forEach(bookmark -> {
            if (bookmark.title().contains(s)) {
                result.add(bookmark);
            }
        });

        return result;
    }

    @Override
    public void cleanUp(String username) {
        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }

        updateGroups(username);

        bookmarkGroups.get(username).forEach(group -> cleanUpGroup(username, group.getName()));
    }

    @Override
    public void importFromChrome(String username) {
        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }

        if(!isExistingGroup(username, "chrome")){
            addGroup(username, new Group("chrome"));
        }
        List<Bookmark> bookmarks = BOOKMARKS_EXTRACTOR.extract();
        try (Writer writer = new StringWriter()) {
            for (Bookmark bookmark : bookmarks) {
                String line = GSON.toJson(bookmark) + "\n";
                writer.write(line);
            }
            Files.write(Paths.get(ROOT + "bookmarks\\" + username + "\\chrome.txt"), writer.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cleanUpGroup(String username, String groupName){
        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }

        updateGroup(username, groupName);
        Group group = getGroup(username, groupName);
        List<Bookmark> bookmarks = group.getBookmarks();
        List<Bookmark> result = new ArrayList<>();

        bookmarks.forEach(bookmark -> {
            if (isValidUrl(bookmark.url())) {
                result.add(bookmark);
            }
        });

        String path = ROOT + "bookmarks\\" + username + "\\" + groupName + ".txt";

        try (Writer writer = new StringWriter()) {
            for (Bookmark bookmark : result) {
                String line = GSON.toJson(bookmark) + "\n";
                writer.write(line);
            }
            Files.write(Paths.get(path), writer.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    boolean isValidUrl(String url){
        try {
            return HttpHandler.checkIfValidUrl(url);
        } catch (IOException e) {
            return false;
        }
    }
}
