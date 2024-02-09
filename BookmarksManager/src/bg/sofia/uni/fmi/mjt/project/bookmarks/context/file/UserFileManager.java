package bg.sofia.uni.fmi.mjt.project.bookmarks.context.file;

import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserFileManager implements FileHandler {
    public static void addNew(String pathStr, String line, String newPathStr,
                              Map<String, User> cachedRegisteredUsers,
                              Map<String, List<Group>> cachedBookmarkGroups,
                              User user) {
        try (Writer writer = new StringWriter()) {
            writer.write(line);
            Path path = Paths.get(pathStr).toAbsolutePath();
            Files.write(path, writer.toString().getBytes(), java.nio.file.StandardOpenOption.APPEND);
            Path newPath = Paths.get(newPathStr);
            Files.createDirectories(newPath);
            cachedRegisteredUsers.put(user.username(), user);
            cachedBookmarkGroups.put(user.username(), new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void load(String path,
                            Map<String, User> cachedRegisteredUsers,
                            Map<String, List<Group>> cachedBookmarkGroups) {
        try (Reader reader = new FileReader(Paths.get(path).toAbsolutePath().toFile());
            var r = new BufferedReader(reader);) {
            String line;
            while ((line = r.readLine()) != null && !line.isBlank()) {
                String[] tokens = line.split(" ");
                String username = tokens[0];
                String hashedPassword = tokens[1];
                String id = tokens[2];

                cachedRegisteredUsers.put(username, new User(username, hashedPassword, id));
                cachedBookmarkGroups.put(username, new ArrayList<>());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
