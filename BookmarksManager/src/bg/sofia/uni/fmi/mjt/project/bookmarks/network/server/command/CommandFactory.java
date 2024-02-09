package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.AddBookmarkCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.AddGroupCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.CleanupCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.ImportFromChromeCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.ListBookmarksCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.RemoveBookmarkCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks.SearchBookmarksCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user.ExitCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user.LogoutCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user.RegisterCommand;

public class CommandFactory {
    public static Command of(CommandType command, String[] arguments, String sessionId) {
        return switch (command) {
            case REGISTER -> new RegisterCommand(command, arguments, sessionId);
            case LOGIN -> new LoginCommand(command, arguments, sessionId);
            case LOGOUT -> new LogoutCommand(command, arguments, sessionId);
            case NEW_GROUP -> new AddGroupCommand(command, arguments, sessionId);
            case ADD -> new AddBookmarkCommand(command, arguments, sessionId);
            case REMOVE -> new RemoveBookmarkCommand(command, arguments, sessionId);
            case LIST -> new ListBookmarksCommand(command, arguments, sessionId);
            case SEARCH -> new SearchBookmarksCommand(command, arguments, sessionId);
            case CLEANUP -> new CleanupCommand(command, arguments, sessionId);
            case IMPORT -> new ImportFromChromeCommand(command, arguments, sessionId);
            case EXIT -> new ExitCommand(command, arguments, sessionId);
            default -> new UnknownCommand(command, arguments, sessionId);
        };
    }
}