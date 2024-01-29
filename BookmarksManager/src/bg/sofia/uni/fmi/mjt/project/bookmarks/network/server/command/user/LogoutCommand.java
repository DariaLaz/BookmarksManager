package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandBase;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;

public class LogoutCommand extends CommandBase {
    public LogoutCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    @Override
    public Response execute() {
        if (session.logout(getSessionId())) {
            return new Response("Successfully logged out", true, null, getCommand());
        }
        return new Response("Something went wrong. Try again!", false, null, getCommand());
    }

}
