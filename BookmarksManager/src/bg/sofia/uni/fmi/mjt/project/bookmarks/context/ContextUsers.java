package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import java.io.Reader;

public interface ContextUsers {
    void load(Reader reader);
    boolean isRegistered(String username);
    boolean isCorrectPassword(String username, String password);
    void addNewUser(String username, String hashedPassword);
}
