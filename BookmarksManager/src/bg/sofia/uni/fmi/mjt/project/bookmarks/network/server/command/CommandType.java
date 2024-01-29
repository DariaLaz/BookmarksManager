package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

public enum CommandType {
    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout"),
    NEW_GROUP("new-group"),
    ADD("add-to"),
    REMOVE("remove-from"),
    LIST("list"),
    SEARCH("search"),
    CLEANUP("cleanup"),
    IMPORT("import-from-chrome"),
    UNKNOWN_COMMAND("Unknown command");


    private final String command;

    CommandType(String command) {
        this.command = command;
    }

    public static CommandType of(String command) {
        for (CommandType cmd : CommandType.values()) {
            if (cmd.command.equals(command)) {
                return cmd;
            }
        }
        return UNKNOWN_COMMAND;
    }

    public String getCommand() {
        return command;
    }
}
