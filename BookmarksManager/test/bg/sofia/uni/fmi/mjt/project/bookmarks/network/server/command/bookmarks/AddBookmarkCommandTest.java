package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.UnknownCommand;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks.BookmarkManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class AddBookmarkCommandTest {
    private static final String SESSION_ID = "session-id";
    private static final String SESSION_TRUE = "session-true";
    private static final String SESSION_FALSE = "session-false";
    private static final String GROUP_NAME = "group-name";
    private static final String URL = "https://www.google.com";
    static BookmarkManager bookmarkManagerMocked;
    @BeforeAll
    static void setUp() throws AlreadyExistingException {
        bookmarkManagerMocked = mock(BookmarkManager.class);
        when(bookmarkManagerMocked.addBookmark(SESSION_TRUE, GROUP_NAME, URL, false)).thenReturn(true);
        when(bookmarkManagerMocked.addBookmark(SESSION_FALSE, GROUP_NAME, URL, true)).thenReturn(false);
    }

    @Test
    void testGetGroupNameWithoutArguments() {
        AddBookmarkCommand addBookmarkCommand = new AddBookmarkCommand(CommandType.ADD, new String[]{}, SESSION_ID);
        assertThrows(IllegalArgumentException.class, addBookmarkCommand::getGroupName,
                "Should throw IllegalArgumentException when Group name is missing");
    }

    @Test
    void testGettersWithoutArguments() {
        String[] arguments = new String[0];
        AddBookmarkCommand addBookmarkCommand = new AddBookmarkCommand(CommandType.ADD, arguments, "");
        assertThrows(IllegalArgumentException.class, addBookmarkCommand::getBookmarkUrl,
                "Should throw IllegalArgumentException when URL is missing");
        assertThrows(IllegalArgumentException.class, addBookmarkCommand::getGroupName,
                "Should throw IllegalArgumentException when Group name is missing");
    }

    @Test
    void testIsShortenedUrlWithShortenedUrl() throws UnknownCommand {
        AddBookmarkCommand addBookmarkCommand = new AddBookmarkCommand(CommandType.ADD,
                new String[]{GROUP_NAME, URL, "shorten"}, "");
        assertTrue(addBookmarkCommand.isShorten(),"Should return true when the URL is shortened");
    }
    @Test
    void testGetUrlWithoutHttp() {
        AddBookmarkCommand addBookmarkCommand = new AddBookmarkCommand(CommandType.ADD,
                new String[]{GROUP_NAME,"www.google.com"}, "");
        assertEquals(URL, addBookmarkCommand.getBookmarkUrl(),"Should return the URL starting with https://");
    }
    @Test
    void testExecuteWithSuccessfulAddBookmark() {
        try (var staticMock = mockStatic(BookmarkManager.class)) {
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManagerMocked);

            Response response = new Response("Successfully added bookmark", true, SESSION_TRUE, CommandType.ADD);

            AddBookmarkCommand addBookmarkCommand = new AddBookmarkCommand(CommandType.ADD,
                    new String[]{GROUP_NAME, URL}, SESSION_TRUE);
            assertEquals(response, addBookmarkCommand.execute(),"Should return a successful response");
        }

    }
    @Test
    void testExecuteWithUnsuccessfulAddBookmark() {
        try (var staticMock = mockStatic(BookmarkManager.class)) {
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManagerMocked);

            Response response = new Response("Something went wrong. Try again!", false, SESSION_FALSE, CommandType.ADD);

            AddBookmarkCommand addBookmarkCommand = new AddBookmarkCommand(CommandType.ADD,
                    new String[]{GROUP_NAME, URL}, SESSION_FALSE);
            assertEquals(response, addBookmarkCommand.execute(),"Should return an unsuccessful response");
        }
    }
}
