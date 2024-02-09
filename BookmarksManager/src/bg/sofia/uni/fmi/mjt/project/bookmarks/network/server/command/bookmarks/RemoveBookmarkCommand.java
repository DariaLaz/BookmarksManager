package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.Logger;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;

public class RemoveBookmarkCommand extends BookmarkCommand  {
    public RemoveBookmarkCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    String getGroupName() {
        try {
            return getArguments()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Group name is required");
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
            throw new IllegalArgumentException("Bookmark url is required");
        }
    }

    @Override
    public Response execute() {
        try {
            if (BOOKMARK_HANDLER.removeBookmark(getSessionId(), getGroupName(), getBookmarkUrl())) {
                return new Response("Successfully removed bookmark", true, null, getCommand());
            }
        } catch (Exception e) {
            LOGGER.log(e);
            return new Response(e.getMessage(), false, null, getCommand());
        }
        return new Response("Something went wrong. Try again!", false, null, getCommand());
    }
}
