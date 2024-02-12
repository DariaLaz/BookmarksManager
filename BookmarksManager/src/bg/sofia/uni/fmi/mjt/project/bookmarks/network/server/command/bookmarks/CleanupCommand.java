package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class CleanupCommand extends BookmarkCommand {
    private static final int MAX_ARGUMENTS_COUNT = 0;
    public CleanupCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    public void validateArguments() {
        if (getArguments().length != MAX_ARGUMENTS_COUNT) {
            throw new IllegalArgumentException(Messages.INVALID_ARGUMENTS);
        }
    }

    @Override
    public Response execute() {
        validateArguments();
        try {
            String username = SessionManager.getUsername(getSessionId());
            if (BOOKMARK_HANDLER.cleanUp(username)) {
                return new Response(Messages.SUCCESSFUL_CLEANED_UP, true, getSessionId(), getCommand());
            }
        } catch (Exception e) {
            LOGGER.log(e, getSessionId());
            return new Response(e.getMessage(), false, getSessionId(), getCommand());
        }
        return new Response(Messages.UNSUCCESSFUL_EXECUTION, false, getSessionId(), getCommand());
    }
}
