package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks.BookmarkManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ListBookmarksCommandTest {
    private static final String SESSION_ID = "sessionId";
    private static BookmarkManager bookmarkManager;
    private static MockedStatic<SessionManager> staticMockSession;
    private static String USERNAME = "username";
    @BeforeAll
    static void setUp() {
        bookmarkManager = mock(BookmarkManager.class);
        when(bookmarkManager.listBookmarks(USERNAME, Optional.empty()))
                .thenReturn(List.of());

        staticMockSession = mockStatic(SessionManager.class);
        staticMockSession.when(() -> SessionManager.getUsername(SESSION_ID))
                .thenReturn(USERNAME);

    }

    @AfterAll
    static void tearDown() {
        staticMockSession.close();
    }

    @Test
    void testGetGroupNameWithoutArguments() throws UnknownCommand {
        ListBookmarksCommand listBookmarksCommand = new ListBookmarksCommand(CommandType.LIST, new String[]{}, "id");
        assertTrue(listBookmarksCommand.getGroupName().isEmpty(),"Should return an empty optional when there are no arguments");
    }

    @Test
    void testGetGroupNameWithoutGroupName() throws UnknownCommand {
        ListBookmarksCommand listBookmarksCommand = new ListBookmarksCommand(CommandType.LIST, new String[]{"--group-name"}, "id");
        assertThrows(IllegalArgumentException.class, () -> listBookmarksCommand.getGroupName());
    }

    @Test
    public void testExecuteSuccess() {
        try(var staticMock = mockStatic(BookmarkManager.class)) {
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManager);

            ListBookmarksCommand listBookmarksCommand = new ListBookmarksCommand(CommandType.LIST, new String[]{}, SESSION_ID);
            Response response = new Response("[]",true, SESSION_ID, CommandType.LIST);
            assertEquals(response, listBookmarksCommand.execute(),"Should return a successful response");
        }
    }
}
