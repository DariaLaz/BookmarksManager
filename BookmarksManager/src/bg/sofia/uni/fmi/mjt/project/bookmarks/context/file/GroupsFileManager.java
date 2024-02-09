package bg.sofia.uni.fmi.mjt.project.bookmarks.context.file;

import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GroupsFileManager {
    private static final Gson GSON = new Gson();

    public static synchronized void updateGroup(String path, Group group) {
        try (Reader reader = new FileReader(path);
             var r = new BufferedReader(reader);) {
            String line;
            while ((line = r.readLine()) != null && !line.isBlank()) {
                Bookmark b = GSON.fromJson(line, Bookmark.class);
                if (group.getBookmarks().stream()
                        .noneMatch(book -> book.url().equals(b.url()))) {
                    group.addBookmark(b);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void updateGroups(String path, List<Group> groups) {
        try {
            Files.walk(Paths.get(path)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String fileName = filePath.getFileName().toString().replace(".txt", "");
                    if (groups.stream().noneMatch(gr -> gr.getName().equals(fileName))) {
                        groups.add(new Group(fileName));
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void removeBookmark(String path, String url, Group group) {
        try (Writer writer = new StringWriter()) {
            Files.write(Paths.get(path), writer.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void appendToFile(String path, List<Bookmark> bookmarks) {
        try (Writer writer = new StringWriter()) {
            for (Bookmark bookmark : bookmarks) {
                String line = GSON.toJson(bookmark) + "\n";
                writer.write(line);
            }
            Files.write(Paths.get(path), writer.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void addNewBookmark(String path, String line) {
        try (Writer writer = new StringWriter()) {
            writer.write(line);
            Files.write(Paths.get(path), writer.toString().getBytes(), java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
