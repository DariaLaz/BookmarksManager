package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.file.LoggerFileManager;

public class Logger {
    private static Logger instance = null;
    private Logger() {
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(Exception e) {
        LoggerFileManager.log(e);
    }
}
