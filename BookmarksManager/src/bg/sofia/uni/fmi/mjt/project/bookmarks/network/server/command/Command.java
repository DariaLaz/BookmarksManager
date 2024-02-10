package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;

public interface Command {
    /**
     * Returns the command type.
     *
     * @return the command type
     */
    CommandType getCommand();

    /**
     * Returns the command arguments.
     *
     * @return the command arguments
     */
    String[] getArguments();

    /**
     * Returns the session id.
     *
     * @return the session id
     */
    String getSessionId();

    /**
     * Executes the command.
     *
     * @return the response
     */
    Response execute();
}
