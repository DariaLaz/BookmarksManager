package bg.sofia.uni.fmi.mjt.project.bookmarks.network;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;

public record Response(String message, boolean successful, String sessionID, CommandType commandType){
}
