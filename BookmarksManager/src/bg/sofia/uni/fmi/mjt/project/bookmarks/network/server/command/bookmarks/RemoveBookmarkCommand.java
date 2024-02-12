package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class RemoveBookmarkCommand extends BookmarkCommand  {
    private static final int REQUIRED_NUMBER_OF_ARGUMENTS = 2;
    public void validateArguments() {
        if (getArguments().length != REQUIRED_NUMBER_OF_ARGUMENTS) {
            throw new IllegalArgumentException(Messages.INVALID_ARGUMENTS);
        }
    }

    public RemoveBookmarkCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    String getGroupName() {
        try {
            return getArguments()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(Messages.GROUP_NAME_REQUIRED);
        }
    }

    String getBookmarkUrl() {
        try {
            var url = getArguments()[1];
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            return url;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(Messages.BOOKMARK_URL_REQUIRED);
        }
    }

    @Override
    public Response execute() {
        validateArguments();
        try {
            if (BOOKMARK_HANDLER.removeBookmark(getSessionId(), getGroupName(), getBookmarkUrl())) {
                return new Response(Messages.SUCCESSFUL_REMOVE_BOOKMARK, true, getSessionId(), getCommand());
            }
        } catch (Exception e) {
            LOGGER.log(e, getSessionId());
            return new Response(e.getMessage(), false, getSessionId(), getCommand());
        }
        return new Response(Messages.UNSUCCESSFUL_EXECUTION, false, getSessionId(), getCommand());
    }
}
