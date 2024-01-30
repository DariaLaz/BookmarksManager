package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;

import java.util.List;

public class SearchBookmarksCommand extends BookmarkCommand {
    public SearchBookmarksCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    SearchType getSearchType() {
        return SearchType.of(getArguments()[0]);
    }

    List<String> searchArguments() {
        String[] arguments = new String[getArguments().length - 1];
        System.arraycopy(getArguments(), 1, arguments, 0, getArguments().length - 1);
        return List.of(arguments);
    }

    @Override
    public Response execute() {
        try {
            String username = SessionManager.getUsername(getSessionId());
            List<Bookmark> bookmarks = BOOKMARK_HANDLER.search(username, getSearchType(), searchArguments());
            String str = GSON.toJson(bookmarks);
            return new Response(str, true, getSessionId(), getCommand());
        } catch (Exception e) {
            return new Response(e.getMessage(), false, null, getCommand());
        }
    }
}
