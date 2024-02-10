package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class UnknownCommand extends CommandBase {

    public UnknownCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    @Override
    public Response execute() {
        return new Response(Messages.UNKNOWN_COMMAND, false, null, CommandType.UNKNOWN_COMMAND);
    }
}
