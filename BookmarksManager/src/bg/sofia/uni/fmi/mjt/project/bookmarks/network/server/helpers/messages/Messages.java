package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.messages;

public class Messages {
    public static final String INVALID_ARGUMENTS = "Invalid arguments";
    public static final String UNSUCCESSFUL_EXECUTION = "Something went wrong. Try again!";
    public static final String UNKNOWN_COMMAND = "Unknown command";
    public static final String CANNOT_BE_NULL_EMPTY = "%s cannot be null or empty";
    public static final String CANNOT_BE_EMPTY = "%s cannot be empty";
    public static final String SESSION_NOT_FIND = "Cannot get username for current session";

    // User messages
    public static final String SUCCESSFUL_LOGOUT = "Successfully logged out";
    public static final String SUCCESSFUL_LOGIN = "Successfully logged in";
    public static final String SUCCESSFUL_REGISTRATION = "Successfully registered";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String USERNAME_REQUIRED = "Username is required";
    public static final String WRONG_USERNAME_OR_PASSWORD = "Wrong username or password";
    public static final String BYE_MESSAGE = "Bye!";
    public static final String ALREADY_LOGGED_IN = "You are already logged in";
    public static final String USER_NOT_REGISTERED = "%s is not registered";
    public static final String USER_PASS_NOT_CORRECT = "Cannot login user %s because the password is incorrect";
    public static final String SHOULD_LOGIN_FIRST = "You are not logged in. Please login first";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String SHOULD_NOT_BE_LOGGED = "You should not be logged in for this option!";

    // Bookmark messages
    public static final String GROUP_NAME_REQUIRED = "Group name is required";
    public static final String BOOKMARK_URL_REQUIRED = "Bookmark url is required";
    public static final String SUCCESSFUL_ADD_BOOKMARK = "Successfully added bookmark";
    public static final String SUCCESSFUL_REMOVE_BOOKMARK = "Successfully removed bookmark";
    public static final String SUCCESSFUL_ADD_GROUP = "Successfully added group";
    public static final String SUCCESSFUL_CLEANED_UP = "Successfully cleaned up";
    public static final String SUCCESSFUL_IMPORT = "Successfully imported from Chrome";
    public static final String UNSUCCESSFUL_ADD_GROUP = "Cannot add group \"%s\" because it already exists";
    public static final String URL_ALREADY_EXISTS = "%s already exists";
    public static final String GROUP_DOES_NOT_EXIST = "Group does not exist";
    public static final String BOOKMARK_DOES_NOT_EXIST = "Bookmark does not exist";

    // Bitly messages
    public static final String UNSUCCESSFUL_SHORTEN = "Cannot create shortened url.";

}
