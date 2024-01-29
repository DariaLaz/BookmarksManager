package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;

public interface Command {
    CommandType getCommand();
    String[] getArguments();
    String getSessionId();
    Response execute();
}
