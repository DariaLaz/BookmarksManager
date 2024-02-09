package bg.sofia.uni.fmi.mjt.project.bookmarks.context.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LoggerFileManager implements FileHandler {
    private static final String LOG_ROOT = "src\\bg\\sofia\\uni\\fmi\\mjt\\project\\bookmarks\\context\\logs\\";
    public static void log(Exception e) {
        String name = "log_" + String.valueOf(System.currentTimeMillis());
        String path = LOG_ROOT + name + ".txt";
        FileManager.createFolder(path);
        try (var writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(e.getMessage() + System.lineSeparator() +
                    Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining(System.lineSeparator()))
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
