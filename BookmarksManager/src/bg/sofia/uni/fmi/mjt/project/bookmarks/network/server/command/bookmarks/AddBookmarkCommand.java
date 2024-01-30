package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;

public class AddBookmarkCommand extends BookmarkCommand {
    public static final int GROUP_NAME_INDEX = 0;
    public static final int BOOKMARK_URL_INDEX = 1;
    public static final int SHORTEN_INDEX = 2;
    public static final String SHORTEN_STR_ARGUMENT = "shorten";
    public static final int MAX_SIZE = 3;
    public AddBookmarkCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    public String getGroupName() {
        try {
            return getArguments()[GROUP_NAME_INDEX];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Group name is required");
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
            throw new IllegalArgumentException("Bookmark url is required");
        }
    }

    public boolean isShorten() throws UnknownCommand {
        if (getArguments().length < MAX_SIZE) {
            return false;
        }
        if (getArguments()[SHORTEN_INDEX].equals(SHORTEN_STR_ARGUMENT)) {
            return true;
        }
        throw new UnknownCommand("Unknown argument");
    }

    @Override
    public Response execute() {
        try {
            if (BOOKMARK_HANDLER.addBookmark(getSessionId(), getGroupName(), getBookmarkUrl(), isShorten())) {
                return new Response("Successfully added bookmark", true, null, getCommand());
            }
        } catch (Exception e) {
            return new Response(e.getMessage(), false, null, getCommand());
        }
        return new Response("Something went wrong. Try again!", false, null, getCommand());
    }
}
