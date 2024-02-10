package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Request;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation.Validator;

import java.util.Arrays;
import java.util.function.Predicate;

public class CommandExecutor {
    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String LOGOUT = "logout";
    private static final String NEW_GROUP = "new-group";
    private static final String ADD = "add-to";
    private static final String REMOVE = "remove-from";
    private static final String LIST = "list";
    private static final String SEARCH = "search";
    private static final String CLEANUP = "cleanup";
    private static final String IMPORT = "import-from-chrome";
    private static final String EXIT = "exit";

    private static CommandDetails parse(Request request) throws UnknownCommand {
        if (request == null) {
            return null;
        }
        var params = Arrays.stream(request.input().split("\\s+")).filter(Predicate.not(String::isBlank)).toList();
        if (params.isEmpty()) {
            throw new UnknownCommand(Messages.INVALID_ARGUMENTS);
        }
        CommandType command = CommandType.of(params.get(0));
        var args = params.subList(1, params.size()).toArray(String[]::new);
        String sessionId = request.sessionId();

        Validator.validateAuth(command, sessionId);

        return switch (command) {
            case REGISTER -> new CommandDetails(REGISTER, args, sessionId);
            case LOGIN -> new CommandDetails(LOGIN, args, sessionId);
            case LOGOUT -> new CommandDetails(LOGOUT, args, sessionId);
            case NEW_GROUP -> new CommandDetails(NEW_GROUP, args, sessionId);
            case ADD -> new CommandDetails(ADD, args, sessionId);
            case REMOVE -> new CommandDetails(REMOVE, args, sessionId);
            case LIST -> new CommandDetails(LIST, args, sessionId);
            case SEARCH -> new CommandDetails(SEARCH, args, sessionId);
            case CLEANUP -> new CommandDetails(CLEANUP, args, sessionId);
            case IMPORT -> new CommandDetails(IMPORT, args, sessionId);
            case EXIT -> new CommandDetails(EXIT, args, sessionId);
            default -> new CommandDetails(Messages.UNKNOWN_COMMAND, null, sessionId);
        };
    }

    private Response execute(CommandDetails cmd) {
        try {
            return CommandFactory.of(CommandType.of(cmd.command()), cmd.arguments(), cmd.sessionId()).execute();
        } catch (Exception e) {
            return new Response(e.getMessage(), false, null, CommandType.UNKNOWN_COMMAND);
        }
    }

    public Response handle(Request input) throws UnknownCommand {
        CommandDetails cmd = parse(input);
        return execute(cmd);
    }
}