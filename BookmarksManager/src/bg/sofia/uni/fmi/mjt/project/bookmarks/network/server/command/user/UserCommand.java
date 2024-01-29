package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandBase;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionHandler;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;

public abstract class UserCommand extends CommandBase {
    protected static final SessionHandler session = SessionManager.getInstance();
    public UserCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }
}
