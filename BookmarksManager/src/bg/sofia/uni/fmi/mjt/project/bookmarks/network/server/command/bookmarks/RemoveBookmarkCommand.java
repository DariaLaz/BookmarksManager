package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

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
            return getArguments()[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Bookmark url is required");
        }
    }

    @Override
    public Response execute() {
        try{
            if (bookmarkHandler.removeBookmark(getSessionId(), getGroupName(), getBookmarkUrl())) {
                return new Response("Successfully removed bookmark", true, null, getCommand());
            }
        }
        catch (Exception e) {
            return new Response(e.getMessage(), false, null, getCommand());
        }
        return new Response("Something went wrong. Try again!", false, null, getCommand());
    }
}
