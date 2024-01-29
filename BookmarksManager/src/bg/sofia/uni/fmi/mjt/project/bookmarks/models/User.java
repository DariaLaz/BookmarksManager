package bg.sofia.uni.fmi.mjt.project.bookmarks.models;

public record User(String username, String password, String userId) {
    public User(String username, String password) {
        this(username, password, null);
    }
}
