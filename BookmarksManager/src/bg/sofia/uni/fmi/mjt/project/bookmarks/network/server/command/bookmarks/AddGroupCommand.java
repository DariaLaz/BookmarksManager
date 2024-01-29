package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextBookmarks;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextData;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandBase;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;

public class AddGroupCommand extends CommandBase {
    private static final ContextBookmarks bookmarks = ContextData.getInstance();
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
        try{
            String username = session.getUsername(getSessionId());
            if (bookmarkHandler.addGroup(username, getGroupName())) {
                return new Response("Successfully added group", true, null, CommandType.NEW_GROUP);
            }
        }
        catch (Exception e) {
            return new Response(e.getMessage(), false, null, CommandType.NEW_GROUP);
        }
        return new Response("Something went wrong. Try again!", false, null, CommandType.NEW_GROUP);
    }
}
