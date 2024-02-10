package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class LogoutCommand extends UserCommand {
    public LogoutCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    @Override
    public Response execute() {
        if (SESSION.logout(getSessionId())) {
            return new Response(Messages.SUCCESSFUL_LOGOUT, true, null, getCommand());
        }
        return new Response(Messages.UNSUCCESSFUL_EXECUTION, false, null, getCommand());
    }

}
