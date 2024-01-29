package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandBase;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnvalidParams;

public class LoginCommand extends UserCommand {
    public LoginCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }
    @Override
    public Response execute() {
        try {
            if (session.login(getUsername(), getPassword())) {
                return new Response("Successfully logged in", true, session.getSessionID(getUsername()), CommandType.LOGIN);
            }
        } catch (AlreadyExistingException e) {
            return new Response(e.getMessage(), false, null, getCommand());
        }
        return new Response("Wrong username or password", false, null, getCommand());
    }

    public String getUsername() {
        try {
            return getArguments()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnvalidParams("Username is required");
        }
    }

    public String getPassword() {
        try {
            return getArguments()[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnvalidParams("Password is required");
        }
    }
}
