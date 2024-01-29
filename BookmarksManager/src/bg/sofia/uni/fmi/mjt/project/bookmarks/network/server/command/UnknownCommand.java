package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;

public class UnknownCommand extends CommandBase {

    public UnknownCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    @Override
    public Response execute() {
        return new Response("Unknown command", false, null, CommandType.UNKNOWN_COMMAND);
    }
}
