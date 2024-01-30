package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

public enum SearchType {
    TITLE("--title"),
    TAG("--tags");

    private final String type;

    SearchType(String type) {
        this.type = type;
    }

    public static SearchType of(String type) {
        for (SearchType searchType : SearchType.values()) {
            if (searchType.type.equalsIgnoreCase(type)) {
                return searchType;
            }
        }
        throw new IllegalArgumentException("No search type with name " + type + " found");
    }
}
