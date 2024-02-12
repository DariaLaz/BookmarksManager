package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks.BookmarkManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class CleanupCommandTest {
    private static final String SESSION_FALSE = "sessionFalse";
    private static final String SESSION_TRUE = "sessionTrue";
    private static final String SESSION_EX = "sessionEx";
    private static BookmarkManager bookmarkManager;
    private static MockedStatic<SessionManager> staticMockSession;
    @BeforeAll
    static void setUp() {
        bookmarkManager = mock(BookmarkManager.class);
        when(bookmarkManager.cleanUp(Mockito.anyString()))
                .thenReturn(true);
        when(bookmarkManager.cleanUp("false"))
                .thenReturn(false);
        when(bookmarkManager.cleanUp("true"))
                .thenReturn(true);
        when(bookmarkManager.cleanUp(SESSION_EX))
                .thenThrow(NullPointerException.class);

        staticMockSession = mockStatic(SessionManager.class);
        staticMockSession.when(() -> SessionManager.getUsername(SESSION_FALSE))
                .thenReturn("false");
        staticMockSession.when(() -> SessionManager.getUsername(SESSION_TRUE))
                .thenReturn("true");

    }

    @AfterAll
    static void tearDown() {
        staticMockSession.close();
    }

    @Test
    void testExecute() {
        try(var staticMock = mockStatic(BookmarkManager.class)) {
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManager);

            CleanupCommand cleanupCommand = new CleanupCommand(CommandType.CLEANUP, new String[]{}, SESSION_TRUE);
            Response response = new Response("Successfully cleaned up", true, SESSION_TRUE, CommandType.CLEANUP);
            assertEquals(response, cleanupCommand.execute(),"Should return a successful response");
        }
    }

    @Test
    void testExecuteWithFalse() {
        try(var staticMock = mockStatic(BookmarkManager.class)) {
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManager);

            CleanupCommand cleanupCommand = new CleanupCommand(CommandType.CLEANUP, new String[]{}, SESSION_FALSE);
            Response response = new Response("Something went wrong. Try again!", false, SESSION_FALSE, CommandType.CLEANUP);

            assertEquals(response, cleanupCommand.execute(),"Should return a unsuccessful response");
        }
    }
}
