package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.Logger;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;

public class CleanupCommand extends BookmarkCommand {
    public CleanupCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    @Override
    public Response execute() {
        try {
            String username = SessionManager.getUsername(getSessionId());
            if (BOOKMARK_HANDLER.cleanUp(username)) {
                return new Response("Successfully cleaned up", true, getSessionId(), getCommand());
            }
        } catch (Exception e) {
            LOGGER.log(e);
            return new Response(e.getMessage(), false, null, getCommand());
        }
        return new Response("Something went wrong. Try again!", false, null, getCommand());
    }
}
