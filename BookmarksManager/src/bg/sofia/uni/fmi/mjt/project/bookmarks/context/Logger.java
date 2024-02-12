package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.file.LoggerFileManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;

import java.util.Objects;

public class Logger {
    private static Logger instance = null;
    private Logger() {
    }

    public static Logger getInstance() {
        if (Objects.isNull(instance)) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(Exception e, String sessionId) {
        String username = null;
        if (Objects.nonNull(sessionId)) {
            username = SessionManager.getUsername(sessionId);
        }
        LoggerFileManager.log(e, username);
    }
}
