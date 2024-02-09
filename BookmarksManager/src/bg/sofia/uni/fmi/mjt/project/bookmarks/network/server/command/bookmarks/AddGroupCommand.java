package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.Logger;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;

public class AddGroupCommand extends BookmarkCommand {
    public AddGroupCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    String getGroupName() {
        try {
            return getArguments()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Group name is required");
        }
    }

    @Override
    public Response execute() {
        try {
            if (BOOKMARK_HANDLER.addGroup(getSessionId(), getGroupName())) {
                return new Response("Successfully added group", true, null, CommandType.NEW_GROUP);
            }
        } catch (Exception e) {
            LOGGER.log(e);
            return new Response(e.getMessage(), false, null, CommandType.NEW_GROUP);
        }
        return new Response("Something went wrong. Try again!", false, null, CommandType.NEW_GROUP);
    }
}
