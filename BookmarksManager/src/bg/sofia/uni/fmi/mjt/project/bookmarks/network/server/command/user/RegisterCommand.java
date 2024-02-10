package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnvalidParams;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

public class RegisterCommand extends UserCommand {
    public RegisterCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    private String getUsername() {
        try {
            return getArguments()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnvalidParams(Messages.USERNAME_REQUIRED);
        }
    }

    private String getPassword() {
        try {
            return getArguments()[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnvalidParams(Messages.PASSWORD_REQUIRED);
        }
    }

    @Override
    public Response execute() {
        try {
            if (SESSION.register(getUsername(), getPassword())) {
                return new Response(Messages.SUCCESSFUL_REGISTRATION, true, null, getCommand());
            }
        } catch (AlreadyExistingException e) {
            LOGGER.log(e);
            return new Response(e.getMessage(), false, null, getCommand());
        }
        return new Response(Messages.UNSUCCESSFUL_EXECUTION, false, null, getCommand());
    }
}
