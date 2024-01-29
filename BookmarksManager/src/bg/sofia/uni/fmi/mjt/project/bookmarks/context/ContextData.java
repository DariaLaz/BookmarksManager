package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AuthException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.User;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security.Hash;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security.PasswordHash;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation.Validator;

import javax.naming.AuthenticationException;
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
    private static final String USERS_FILE = "D:\\github\\BookmarksManager\\BookmarksManager\\src\\bg\\sofia\\uni\\fmi\\mjt\\project\\bookmarks\\network\\server\\data\\users.txt";


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

    public void addNewUser(String username, String hashedPassword)  {
        String id = generateID();
        String line = username + " " + hashedPassword + " " + id + "\n";
        try (Writer writer = new StringWriter()) {
            writer.write(line);
            Files.write(Paths.get(USERS_FILE), writer.toString().getBytes(), java.nio.file.StandardOpenOption.APPEND);
            Files.createDirectories(Paths.get("D:\\github\\BookmarksManager\\BookmarksManager\\src\\bg\\sofia\\uni\\fmi\\mjt\\project\\bookmarks\\network\\server\\data\\bookmarks\\" + username));
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
    public List<Group> getBookmarkGroups(String username){
        return bookmarkGroups.get(username);
    }

    @Override
    public void addGroup(String username, Group group) {
        Validator.validateString(username, "Username cannot be null or empty");
        try {
            Files.createFile(Paths.get("D:\\github\\BookmarksManager\\BookmarksManager\\src\\bg\\sofia\\uni\\fmi\\mjt\\project\\bookmarks\\network\\server\\data\\bookmarks\\" + username + "\\" + group.getName() + ".txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bookmarkGroups.get(username).add(group);
    }

    @Override
    public boolean isExistingGroup(String username, String groupName) {
        Validator.validateString(username, "Username cannot be null or empty");
        Validator.validateString(groupName, "Group name cannot be null or empty");

        if (!isRegistered(username)) {
            throw new AuthException("User " + username + " is not registered");
        }

        if(bookmarkGroups.containsKey(username) &&
                bookmarkGroups.get(username).stream().anyMatch(group -> group.getName().equals(groupName))){
            return true;
        }

        String path = "D:\\github\\BookmarksManager\\BookmarksManager\\src\\bg\\sofia\\uni\\fmi\\mjt\\project\\bookmarks\\network\\server\\data\\bookmarks\\" + username;
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

        return bookmarkGroups.get(username).stream().anyMatch(group -> group.getName().equals(groupName));
    }
}
