package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

public interface ContextUsers {
    /**
     * Returns if the given username is registered.
     *
     * @param username the username to be checked.
     *
     * @return true if the username is registered, false otherwise.
     */
    boolean isRegistered(String username);

    /**
     * Returns if the given username and password are correct.
     *
     * @param username the username to be checked.
     * @param password the password to be checked.
     *
     * @return true if the username and password are correct, false otherwise.
     */
    boolean isCorrectPassword(String username, String password);

    /**
     * Adds a new user.
     *
     * @param username the username of the user to be added.
     * @param hashedPassword the hashed password of the user to be added.
     *
     */
    void addNewUser(String username, String hashedPassword);
}
