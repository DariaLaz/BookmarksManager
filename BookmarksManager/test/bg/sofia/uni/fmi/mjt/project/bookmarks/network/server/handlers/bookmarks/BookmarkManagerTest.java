package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.ContextData;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.MockedStatic;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class BookmarkManagerTest {
    static ContextData contextData;
    private static final String SESSION_ID = "sessionId";
    private static final String GROUP = "group";
    private static final String URL = "https://www.google.com";
    private static final String EXISTING_URL = "https://www.github.com";
    private static final String NON_EXISTING_URL = "https://www.google.com";
    private static final String EXISTING = "existing";
    private static final String NON_EXISTING = "non-existing";
    private static MockedStatic<SessionManager> sessionManagerMockedStatic;
    private static MockedStatic<ContextData> contextDataMockedStatic;

    @BeforeAll
    public static void setUp() {
        contextData = mock(ContextData.class);
        when(contextData.isExistingGroup(EXISTING, "group")).thenReturn(true);
        when(contextData.isExistingGroup(NON_EXISTING, "group")).thenReturn(false);
        doNothing().when(contextData).addGroup(anyString(), any());
        doNothing().when(contextData).addBookmark(anyString(), anyString(), any());

        when(contextData.isExistingBookmark(EXISTING, EXISTING, EXISTING_URL)).thenReturn(true);
        when(contextData.isExistingBookmark(EXISTING, EXISTING, NON_EXISTING_URL)).thenReturn(false);

        when(contextData.isExistingGroup(EXISTING, EXISTING)).thenReturn(true);
        when(contextData.isExistingGroup(EXISTING, NON_EXISTING)).thenReturn(false);

        SessionManager sessionManager = mock(SessionManager.class);
        sessionManagerMockedStatic = mockStatic(SessionManager.class);
        sessionManagerMockedStatic.when(() -> SessionManager.getUsername(EXISTING)).thenReturn(EXISTING);
        sessionManagerMockedStatic.when(() -> SessionManager.getUsername(NON_EXISTING)).thenReturn(NON_EXISTING);
        sessionManagerMockedStatic.when(SessionManager::getInstance).thenReturn(sessionManager);

        contextDataMockedStatic = mockStatic(ContextData.class);
        contextDataMockedStatic.when(ContextData::getInstance).thenReturn(contextData);
    }

    @AfterAll
    public static void tearDown() {
        sessionManagerMockedStatic.close();
        contextDataMockedStatic.close();
    }
    @Test
    void testAddGroupWithExistingGroup() {
        SessionManager sessionManager = mock(SessionManager.class);
        sessionManagerMockedStatic.when(() -> SessionManager.getUsername(anyString())).thenReturn(EXISTING);
        sessionManagerMockedStatic.when(SessionManager::getInstance).thenReturn(sessionManager);

        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();

        assertThrows(AlreadyExistingException.class, () -> bookmarkManager.addGroup(SESSION_ID, GROUP));
    }

    @Test
    void testAddBookmarkWithNullOrEmptyArguments() {
        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();

        assertThrows(IllegalArgumentException.class,
                () -> bookmarkManager.addBookmark(SESSION_ID, null, URL, false));
        assertThrows(IllegalArgumentException.class,
                () -> bookmarkManager.addBookmark(SESSION_ID, EXISTING, " ", false));
    }

    @Test
    void testAddBookmarkWithNotExistingGroup() {
        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> bookmarkManager.addBookmark(SESSION_ID, NON_EXISTING, URL, false));
    }

    @Test
    void testAddBookmarkWithExistingBookmark() {
        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();
        assertThrows(AlreadyExistingException.class,
                () -> bookmarkManager.addBookmark(EXISTING, EXISTING, EXISTING_URL, false));
    }

    @Test
    void testAddBookmarkWithCorrectData() throws AlreadyExistingException {
        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();
        assertTrue(bookmarkManager.addBookmark(EXISTING, EXISTING, NON_EXISTING_URL, false));
    }

    @Test
    void testRemoveBookmarkWithNullOrEmptyArguments() {
        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> bookmarkManager.removeBookmark(SESSION_ID, null, URL));
        assertThrows(IllegalArgumentException.class,
                () -> bookmarkManager.removeBookmark(SESSION_ID, EXISTING, " "));
    }

    @Test
    void testRemoveBookmarkWithNotExistingGroup() {
        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> bookmarkManager.removeBookmark(SESSION_ID, NON_EXISTING, URL));
    }

    @Test
    void testRemoveBookmarkWithNotExistingBookmark() {
        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> bookmarkManager.removeBookmark(EXISTING, EXISTING, NON_EXISTING_URL));
    }

    @Test
    void testRemoveBookmarkWithCorrectData() {
        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();
        assertTrue(bookmarkManager.removeBookmark(EXISTING, EXISTING, EXISTING_URL));
    }

    @Test
    void testListBookmarksWithEmptyArguments() {
        BookmarkHandler bookmarkManager = BookmarkManager.getInstance();
        assertThrows(IllegalArgumentException.class, () -> bookmarkManager.listBookmarks(null, Optional.empty()));
        assertThrows(IllegalArgumentException.class, () -> bookmarkManager.listBookmarks(" ", Optional.empty()));
    }
}

