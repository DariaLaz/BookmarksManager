package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class AddGroupCommand extends BookmarkCommand {
    private static final int GROUP_NAME_INDEX = 0;
    private static final int REQUIRED_ARGUMENTS = 1;
    public AddGroupCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    String getGroupName() {
        try {
            return getArguments()[GROUP_NAME_INDEX];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(Messages.GROUP_NAME_REQUIRED);
        }
    }

    public void validateArguments() {
        if (getArguments().length != REQUIRED_ARGUMENTS) {
            throw new IllegalArgumentException(Messages.INVALID_ARGUMENTS);
        }
    }

    @Override
    public Response execute() {
        validateArguments();
        try {
            if (BOOKMARK_HANDLER.addGroup(getSessionId(), getGroupName())) {
                return new Response(Messages.SUCCESSFUL_ADD_GROUP, true, getSessionId(), CommandType.NEW_GROUP);
            }
        } catch (Exception e) {
            LOGGER.log(e, getSessionId());
            return new Response(e.getMessage(), false, getSessionId(), CommandType.NEW_GROUP);
        }
        return new Response(Messages.UNSUCCESSFUL_EXECUTION, false, getSessionId(), CommandType.NEW_GROUP);
    }
}
