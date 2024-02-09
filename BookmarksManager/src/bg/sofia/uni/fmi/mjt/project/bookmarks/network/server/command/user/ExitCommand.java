package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;

public class ExitCommand extends UserCommand {
    public ExitCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    @Override
    public Response execute() {
        if (getSessionId() != null) {
            SESSION.logout(getSessionId());
        }
        return new Response("Bye!", true, getSessionId(), getCommand());
    }
}
