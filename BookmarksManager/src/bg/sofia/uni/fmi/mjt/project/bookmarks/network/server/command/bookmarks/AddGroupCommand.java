package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class AddGroupCommand extends BookmarkCommand {
    public AddGroupCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    String getGroupName() {
        try {
            return getArguments()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(Messages.GROUP_NAME_REQUIRED);
        }
    }

    @Override
    public Response execute() {
        try {
            if (BOOKMARK_HANDLER.addGroup(getSessionId(), getGroupName())) {
                return new Response(Messages.SUCCESSFUL_ADD_GROUP, true, null, CommandType.NEW_GROUP);
            }
        } catch (Exception e) {
            LOGGER.log(e);
            return new Response(e.getMessage(), false, getSessionId(), CommandType.NEW_GROUP);
        }
        return new Response(Messages.UNSUCCESSFUL_EXECUTION, false, getSessionId(), CommandType.NEW_GROUP);
    }
}
