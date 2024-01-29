package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation;

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
}
