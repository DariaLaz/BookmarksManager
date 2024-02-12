package bg.sofia.uni.fmi.mjt.project.bookmarks.context.file;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager implements FileHandler  {
    private static final Gson GSON = new Gson();

    public static void create(String pathStr) {
        try {
            Path path = Paths.get(pathStr);
            Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
