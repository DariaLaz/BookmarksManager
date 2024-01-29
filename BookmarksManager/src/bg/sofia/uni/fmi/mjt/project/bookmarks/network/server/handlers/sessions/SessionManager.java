package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextUsers;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextData;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security.Hash;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security.PasswordHash;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation.Validator;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager implements SessionHandler {
    private static final Map<String, String> activeSessions = new HashMap<>(); // username -> sessionID
    private static final Hash passwordHasher = new PasswordHash();
    private static final Validator VALIDATOR = new Validator();
    protected static final ContextUsers context = ContextData.getInstance();

    private static SessionHandler INSTANCE = null;
    private SessionManager() {

    }
    public static SessionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }

    @Override
    public Map<String, String> getActiveSessions(){
        return activeSessions;
    }

    public static String getUsername(String sessionId) {
        return activeSessions.entrySet().stream()
                .filter(e -> e.getValue().equals(sessionId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new UnknownUser("Cannot get username for sessionID " + sessionId));
    }

    @Override
    public boolean isLogged(String username) {
        return getActiveSessions().containsKey(username);
    }

    @Override
    public synchronized boolean login(String username, String password) throws AlreadyExistingException {
        if (isLogged(username)) {
            throw new AlreadyExistingException("Cannot login user " + username + " because they are already logged in");
        }
        if (isLogged(username)) {
            throw new AlreadyExistingException("Cannot check password for logged user " + username);
        }
        if (!context.isRegistered(username)) {
            throw new UnknownUser("Cannot login user " + username + " because they are not registered");
        }
        if (!context.isCorrectPassword(username, password)) {
            throw new IllegalArgumentException("Cannot login user " + username + " because the password is incorrect");
        }
        String sessionID = UUID.randomUUID().toString();
        setToActiveSessions(username, sessionID);
        return true;
    }

    @Override
    public synchronized boolean logout(String sessionID) {
        Validator.validateString(sessionID, "You are not logged in. Please login first");

        String username = getActiveSessions().entrySet().stream()
                .filter(e -> e.getValue().equals(sessionID))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new UnknownUser("Cannot logout user with sessionID " + sessionID + " because they are not logged in"));
        if (!isLogged(username)) {
            throw new UnknownUser("Cannot logout user " + username + " because they are not logged in");
        }
        removeFromActiveSessions(username);
        return true;
    }

    @Override
    public synchronized boolean register(String username, String password) throws AlreadyExistingException {
        if (context.isRegistered(username)) {
            throw new AlreadyExistingException("Cannot register user with username " + username + " because it already exists");
        }

        String hashedPassword = passwordHasher.hash(password);
        try {
            context.addNewUser(username, hashedPassword);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public String getSessionID(String username) {
        return getActiveSessions().get(username);
    }
    private void setToActiveSessions(String username, String sessionID){
        activeSessions.put(username, sessionID);
    }
    private void removeFromActiveSessions(String username){
        activeSessions.remove(username);
    }

}

