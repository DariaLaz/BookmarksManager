package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class AddBookmarkCommand extends BookmarkCommand {
    public static final int GROUP_NAME_INDEX = 0;
    public static final int BOOKMARK_URL_INDEX = 1;
    public static final int SHORTEN_INDEX = 2;
    public static final String SHORTEN_STR_ARGUMENT = "shorten";
    public static final int MAX_SIZE = 3;

    public void validateArguments() {
        if (getArguments().length > MAX_SIZE) {
            throw new IllegalArgumentException(Messages.INVALID_ARGUMENTS);
        }
    }

    public AddBookmarkCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    public String getGroupName() {
        try {
            return getArguments()[GROUP_NAME_INDEX];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(Messages.GROUP_NAME_REQUIRED);
        }
    }

    public String getBookmarkUrl() {
        try {
            String url = getArguments()[BOOKMARK_URL_INDEX];
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            return url;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(Messages.BOOKMARK_URL_REQUIRED);
        }
    }

    public boolean isShorten() throws UnknownCommand {
        if (getArguments().length < MAX_SIZE) {
            return false;
        }
        if (getArguments()[SHORTEN_INDEX].equals(SHORTEN_STR_ARGUMENT)) {
            return true;
        }
        throw new UnknownCommand(Messages.INVALID_ARGUMENTS);
    }

    @Override
    public Response execute() {
        try {
            if (BOOKMARK_HANDLER.addBookmark(getSessionId(), getGroupName(), getBookmarkUrl(), isShorten())) {
                return new Response(Messages.SUCCESSFUL_ADD_BOOKMARK, true, getSessionId(), getCommand());
            }
        } catch (Exception e) {
            LOGGER.log(e, getSessionId());
            return new Response(e.getMessage(), false, getSessionId(), getCommand());
        }
        return new Response(Messages.UNSUCCESSFUL_EXECUTION, false, getSessionId(), getCommand());
    }
}
