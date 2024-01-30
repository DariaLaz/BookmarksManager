package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation;

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
}
