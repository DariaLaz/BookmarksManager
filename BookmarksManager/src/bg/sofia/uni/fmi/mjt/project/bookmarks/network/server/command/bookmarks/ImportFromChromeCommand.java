package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class ImportFromChromeCommand extends BookmarkCommand {
    private static final int MAX_ARGUMENTS_COUNT = 0;

    public void validateArguments() {
        if (getArguments().length != MAX_ARGUMENTS_COUNT) {
            throw new IllegalArgumentException(Messages.INVALID_ARGUMENTS);
        }
    }

    public ImportFromChromeCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    @Override
    public Response execute() {
        try {
            String username = SessionManager.getUsername(getSessionId());
            if (BOOKMARK_HANDLER.importFromChrome(username)) {
                return new Response(Messages.SUCCESSFUL_IMPORT, true, getSessionId(), getCommand());
            }
        } catch (Exception e) {
            LOGGER.log(e);
            return new Response(e.getMessage(), false, getSessionId(), getCommand());
        }
        return new Response(Messages.UNSUCCESSFUL_EXECUTION, false, getSessionId(), getCommand());
    }
}
