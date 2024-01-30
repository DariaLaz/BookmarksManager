package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Request;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;

import java.util.Arrays;
import java.util.function.Predicate;

public class CommandExecutor {
    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
            "Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\"";

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
            throw new UnknownCommand("Unknown command");
        }

        CommandType command = CommandType.of(params.get(0));
        var args = params.subList(1, params.size()).toArray(String[]::new);
        String sessionId = request.sessionId();

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
            default -> new CommandDetails("Unknown command", null, sessionId);
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

//    private String addToDo(String[] args) {
//        if (args.length != 2) {
//            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, ADD, 2, ADD + " <username> <todo_item>");
//        }
//
//        String user = args[0];
//        String todo = args[1];
//
//        int todoID = storage.add(user, todo);
//        return String.format("Added new To Do with ID %s for user %s", todoID, user);
//    }
//
//    private String complete(String[] args) {
//        if (args.length != 2) {
//            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, COMPLETE, 2,
//                    COMPLETE + " <username> <todo_item_id>");
//        }
//
//        String user = args[0];
//        int todoID;
//        try {
//            todoID = Integer.parseInt(args[1]);
//        } catch (NumberFormatException e) {
//            return "Invalid ID provided for command \"complete-todo\": only integer values are allowed";
//        }
//
//        storage.remove(user, todoID);
//        return String.format("Completed To Do with ID %s for user %s", todoID, user);
//    }
//
//    private String list(String[] args) {
//        if (args.length != 1) {
//            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, LIST, 1, LIST + " <username>");
//        }
//        String user = args[0];
//        var todos = storage.list(user);
//        if (todos.isEmpty()) {
//            return "No To-Do items found for user with name " + user;
//        }
//
//        StringBuilder response = new StringBuilder(String.format("To-Do list of user %s:%n", user));
//        todos.forEach((k, v) -> response.append(String.format("[%d] %s%n", k, v)));
//
//        return response.toString();
//    }
}