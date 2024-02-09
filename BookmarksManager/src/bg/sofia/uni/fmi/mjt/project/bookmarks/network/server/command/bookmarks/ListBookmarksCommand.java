package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.Logger;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;

import java.util.Optional;

public class ListBookmarksCommand extends BookmarkCommand {
    public ListBookmarksCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    Optional<String> getGroupName() throws UnknownCommand {
        try {
            if (getArguments().length > 0) {
                if (!getArguments()[0].equals("--group-name")) {
                    throw new UnknownCommand("Unknown command");
                }
                return Optional.of(getArguments()[1]);
            }
            return Optional.empty();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Group name is required");
        }
    }

    @Override
    public Response execute() {
        try {
            String username = SessionManager.getUsername(getSessionId());
            var bookmarks = BOOKMARK_HANDLER.listBookmarks(username, getGroupName());
            return new Response(GSON.toJson(bookmarks), true, getSessionId(), getCommand());
        } catch (Exception e) {
            LOGGER.log(e);
            return new Response(e.getMessage(), false, null, getCommand());
        }
    }
}
