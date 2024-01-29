package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.AddGroupCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user.LogoutCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user.RegisterCommand;

public class CommandFactory {
    public static Command of(CommandType command, String[] arguments, String sessionId) {
        switch (command) {
            case REGISTER:
                return new RegisterCommand(command, arguments, sessionId);
            case LOGIN:
                return new LoginCommand(command, arguments, sessionId);
            case LOGOUT:
                return new LogoutCommand(command, arguments, sessionId);
            case NEW_GROUP:
                return new AddGroupCommand(command, arguments, sessionId);
//            case "remove-bookmark":
//                return new RemoveBookmarkCommand(command, arguments);
//            case "get-bookmarks":
//                return new GetBookmarksCommand(command, arguments);
//            case "get-bookmarks-by-tag":
//                return new GetBookmarksByTagCommand(command, arguments);
            default:
                return new UnknownCommand(command, arguments, sessionId);
        }
    }
}