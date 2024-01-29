package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security;

public interface Hash {
    String hash(String password);
    boolean verify(String password, String hash);
}
