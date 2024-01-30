package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;

import java.util.Map;

public interface SessionHandler {
    boolean isLogged(String username);

    boolean login(String username, String password) throws AlreadyExistingException;

    boolean logout(String sessionId);

    boolean register(String username, String password) throws AlreadyExistingException;

    String getSessionID(String username);

    Map<String, String> getActiveSessions();
}
