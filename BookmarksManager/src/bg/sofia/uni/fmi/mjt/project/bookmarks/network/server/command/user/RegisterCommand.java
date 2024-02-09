package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.Logger;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnvalidParams;

public class RegisterCommand extends UserCommand {
    public RegisterCommand(CommandType command, String[] arguments, String sessionId) {
        super(command, arguments, sessionId);
    }

    private String getUsername() {
        try {
            return getArguments()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnvalidParams("Username is required");
        }
    }

    private String getPassword() {
        try {
            return getArguments()[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnvalidParams("Password is required");
        }
    }

    @Override
    public Response execute() {
        try {
            if (SESSION.register(getUsername(), getPassword())) {
                return new Response("User registered successfully", true, null, getCommand());
            }
        } catch (AlreadyExistingException e) {
            LOGGER.log(e);
            return new Response(e.getMessage(), false, null, getCommand());
        }
        return new Response("Something went wrong. Try again!", false, null, getCommand());
    }
}
