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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class SearchBookmarksCommandTest {
    private static final String SESSION_ID = "sessionId";
    private static final String USERNAME = "username";
    private static final SearchType SEARCH_TYPE = SearchType.TAG;
    private static BookmarkManager bookmarkManager;
    private static MockedStatic<SessionManager> staticMockSession;
    @BeforeAll
    static void setUp() {
        bookmarkManager = mock(BookmarkManager.class);
        when(bookmarkManager.search(USERNAME, SEARCH_TYPE, List.of("arg1")))
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
    void testExecuteSuccess() {
        try(var staticMock = mockStatic(BookmarkManager.class);) {
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManager);

            Response response = new Response("[]", true, SESSION_ID, CommandType.SEARCH);

            SearchBookmarksCommand searchBookmarksCommand = new SearchBookmarksCommand(
                    CommandType.SEARCH, new String[]{"--tags", "arg1"}, SESSION_ID);

            assertEquals(response, searchBookmarksCommand.execute(),"Should return a successful response");
        }
    }
}
