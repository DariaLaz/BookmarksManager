package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;

public class AddBookmarkCommand extends BookmarkCommand {
    public AddBookmarkCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    public String getGroupName() {
        try {
            return getArguments()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Group name is required");
        }
    }

    public String getBookmarkUrl() {
        try {
            return getArguments()[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Bookmark url is required");
        }
    }

    public boolean isShorten() throws UnknownCommand {
        if (getArguments().length < 3) {
            return false;
        }
        if (getArguments()[2].equals("shorten")) {
            return true;
        }
        throw new UnknownCommand("Unknown argument");
    }
    @Override
    public Response execute() {
        try {
            if (bookmarkHandler.addBookmark(getSessionId(), getGroupName(), getBookmarkUrl(), isShorten())) {
                return new Response("Successfully added bookmark", true, null, getCommand());
            }
        } catch (Exception e) {
            return new Response(e.getMessage(), false, null, getCommand());
        }
        return new Response("Something went wrong. Try again!", false, null, getCommand());
    }
}
