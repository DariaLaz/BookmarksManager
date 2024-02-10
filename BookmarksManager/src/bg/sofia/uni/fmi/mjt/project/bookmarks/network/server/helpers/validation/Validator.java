package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AuthException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages.Messages;

import java.util.List;

public class Validator {

    public Validator() {
    }

    public static void validateNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateString(String string, String message) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateListNotEmpty(List<?> list, String message) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateListSize(List<?> strings, int i, String message) {
        if (strings.size() != i) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateRegistrate(boolean isRegistrated, String message) {
        if (!isRegistrated) {
            throw new AuthException(message);
        }
    }

    public static void validateAuth(CommandType command, String sessionId) {
        if ((command.equals(CommandType.REGISTER) || command.equals(CommandType.LOGIN))) {
            if (sessionId != null) {
                throw new AuthException(Messages.SHOULD_NOT_BE_LOGGED);
            }
        } else if (sessionId == null) {
            throw new AuthException(Messages.SHOULD_LOGIN_FIRST);
        }
    }
}
