package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

public interface ContextUsers {
    boolean isRegistered(String username);

    boolean isCorrectPassword(String username, String password);

    void addNewUser(String username, String hashedPassword);
}
