package bg.sofia.uni.fmi.mjt.project.bookmarks.context.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoggerFileManager implements FileHandler {
    private static final String LOG_ROOT = "src\\bg\\sofia\\uni\\fmi\\mjt\\project\\bookmarks\\context\\logs\\";
    public static void log(Exception e, String username) {
        String name = "log" +
                      (Objects.isNull(username) ? "_" : "_" + username + "_") +
                      String.valueOf(System.currentTimeMillis()) +
                      ".txt";
        String path = LOG_ROOT + name;
        FileManager.create(path);
        try (var writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(e.getMessage() + System.lineSeparator() +
                    (Objects.isNull(username) ? "" : "Username: " + username + System.lineSeparator()) +
                    Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining(System.lineSeparator()))
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
