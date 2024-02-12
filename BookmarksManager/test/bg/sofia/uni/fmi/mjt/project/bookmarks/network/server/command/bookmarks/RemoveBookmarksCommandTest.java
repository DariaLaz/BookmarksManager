package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks.BookmarkManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class RemoveBookmarksCommandTest {
    private static final String GROUP_NAME = "group";
    private static final String URL = "https://www.google.com";
    private static final String SESSION_FALSE = "sessionFalse";
    private static final String SESSION_TRUE = "sessionTrue";
    private static BookmarkManager bookmarkManager;
    @BeforeAll
    static void setUp() {
        bookmarkManager = mock(BookmarkManager.class);
        when(bookmarkManager.removeBookmark(SESSION_FALSE, GROUP_NAME, URL))
                .thenReturn(false);
        when(bookmarkManager.removeBookmark(SESSION_TRUE, GROUP_NAME, URL))
                .thenReturn(true);
    }

    @Test
    void testValidRemoveBookmarksCommand() {
        RemoveBookmarkCommand removeBookmarkCommand = new RemoveBookmarkCommand(CommandType.REMOVE, new String[]{"gr1", "gr2", "gr3"}, "id");
        assertThrows(IllegalArgumentException.class, removeBookmarkCommand::validateArguments);
    }

    @Test
    void testExecuteSuccessful() {
        try(MockedStatic<BookmarkManager> staticMock = mockStatic(BookmarkManager.class);){
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManager);

            RemoveBookmarkCommand removeBookmarkCommand = new RemoveBookmarkCommand(
                    CommandType.REMOVE, new String[]{GROUP_NAME, URL}, SESSION_TRUE);

            Response response = new Response("Successfully removed bookmark", true, SESSION_TRUE, CommandType.REMOVE);
            assertEquals(response, removeBookmarkCommand.execute(),"Should return a successful response");
        }
    }

    @Test
    void testExecuteUnsuccessful() {
        try(MockedStatic<BookmarkManager> staticMock = mockStatic(BookmarkManager.class);){
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManager);

            RemoveBookmarkCommand removeBookmarkCommand = new RemoveBookmarkCommand(CommandType.REMOVE, new String[]{GROUP_NAME, URL}, SESSION_FALSE);

            Response response = new Response("Something went wrong. Try again!", false, SESSION_FALSE, CommandType.REMOVE);
            assertEquals(response, removeBookmarkCommand.execute(),"Should return a unsuccessful response");
        }
    }
}
