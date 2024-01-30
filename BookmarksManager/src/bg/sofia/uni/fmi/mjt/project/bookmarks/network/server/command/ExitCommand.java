package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;

public class ExitCommand extends CommandBase {
    public ExitCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    @Override
    public Response execute() {
        return new Response("Bye!", true, getSessionId(), getCommand());
    }
}
