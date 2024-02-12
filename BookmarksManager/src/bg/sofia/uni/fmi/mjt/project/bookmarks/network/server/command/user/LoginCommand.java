package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnvalidParams;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class LoginCommand extends UserCommand {
    public LoginCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    @Override
    public Response execute() {
        try {
            if (SESSION.login(getUsername(), getPassword())) {
                return new Response(Messages.SUCCESSFUL_LOGIN, true,
                        SESSION.getSessionID(getUsername()), CommandType.LOGIN);
            }
        } catch (AlreadyExistingException e) {
            return new Response(e.getMessage(), false, getSessionId(), getCommand());
        }
        return new Response(Messages.WRONG_USERNAME_OR_PASSWORD, false, getSessionId(), getCommand());
    }

    public String getUsername() {
        try {
            return getArguments()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnvalidParams(Messages.USERNAME_REQUIRED);
        }
    }

    public String getPassword() {
        try {
            return getArguments()[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(e, getSessionId());
            throw new UnvalidParams(Messages.PASSWORD_REQUIRED);
        }
    }
}
