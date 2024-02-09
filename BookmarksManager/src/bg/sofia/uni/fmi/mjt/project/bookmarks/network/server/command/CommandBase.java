package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.Logger;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks.BookmarkHandler;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks.BookmarkManager;


public abstract class CommandBase implements Command {
    private final CommandType command;
    private final String[] arguments;
    private String sessionId;
    protected static final BookmarkHandler BOOKMARK_HANDLER = BookmarkManager.getInstance();
    protected static final Logger LOGGER = Logger.getInstance();

    public CommandBase(CommandType command, String[] arguments, String sessionId) {
        this.command = command;
        this.arguments = arguments;
        this.sessionId = sessionId;
    }

    @Override
    public CommandType getCommand() {
        return command;
    }

    @Override
    public String[] getArguments() {
        return arguments;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public abstract Response execute();
}