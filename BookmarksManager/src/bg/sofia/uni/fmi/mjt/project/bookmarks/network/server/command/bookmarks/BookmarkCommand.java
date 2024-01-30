package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextBookmarks;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextData;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandBase;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks.BookmarkHandler;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks.BookmarkManager;
import com.google.gson.Gson;

public abstract class BookmarkCommand extends CommandBase {
    protected static final BookmarkHandler context = BookmarkManager.getInstance();
    protected static final Gson GSON = new Gson();
    public BookmarkCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }
}
