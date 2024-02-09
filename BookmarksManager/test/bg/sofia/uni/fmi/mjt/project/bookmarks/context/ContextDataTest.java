package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.file.FileManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

public class ContextDataTest {
    //BOOKMARKS FUNCS
    @Test
    void testAddGroupWithNullAndEmptyUsername() {
        Context contextData = ContextData.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> contextData.addGroup(null, new Group("name")),
                "Should throw exception when username is null");
        assertThrows(IllegalArgumentException.class,
                () -> contextData.addGroup(" ", new Group("name")),
                "Should throw exception when username is blank");
        assertThrows(IllegalArgumentException.class,
                () -> contextData.addGroup("", new Group("name")),
                "Should throw exception when username is empty");
    }

    @Test
    void testAddGroupWithCorrectData(){
        Context contextData = Mockito.mock();
        String path = "D:\\github\\BookmarksManager\\BookmarksManager\\src\\bg\\sofia\\uni\\fmi\\mjt\\project\\bookmarks\\network\\server\\data\\bookmarks\\name\\group.txt";

        mockStatic(FileManager.class);
        doNothing().when(FileManager.class);
        FileManager.createFolder(path);

        Mockito.when(contextData.cachedBookmarkGroups()).thenReturn(Map.of("name", new ArrayList<>()));

        assertDoesNotThrow(() -> contextData.addGroup("name", new Group("group")),
                "Should not throw exceptions with correct data");
    }


//    boolean isExistingGroup(String username, String groupName);
//    void addBookmark(String username, String groupName, Bookmark bookmark);
//    boolean isExistingBookmark(String username, String groupName, String url);
//    void removeBookmark(String username, String groupName, String bookmarkUrl);
//    List<Bookmark> getBookmarks(String username, String groupName);
//    List<Bookmark> getBookmarks(String username);
//    List<Bookmark> searchByTag(String username, List<String> strings);
//    List<Bookmark> searchByTitle(String username, String s);
//    void cleanUp(String username);
//    void importFromChrome(String username);
}
