package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.bookmarks.BookmarkManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class AddGroupCommandTest {
    private static final String SESSION_ID = "sessionID";
    private static final String GROUP_NAME = "group";
    private static final String SESSION_FALSE = "sessionFalse";
    private static final String SESSION_TRUE = "sessionTrue";
    private static BookmarkManager bookmarkManager;
    @BeforeAll
    static void setUp() throws AlreadyExistingException {
        bookmarkManager = mock(BookmarkManager.class);
        when(bookmarkManager.addGroup(SESSION_TRUE, GROUP_NAME)).thenReturn(true);
        when(bookmarkManager.addGroup(SESSION_FALSE, GROUP_NAME)).thenReturn(false);
    }
    @Test
    void testGetGroupNameWithoutArguments() {
        AddGroupCommand addGroupCommand = new AddGroupCommand(CommandType.NEW_GROUP, new String[]{}, SESSION_ID);
        assertThrows(IllegalArgumentException.class, addGroupCommand::getGroupName,
                "Should throw IllegalArgumentException when Group name is missing");
    }

    @Test
    void testGetGroupNameWithOneArgument() {
        AddGroupCommand addGroupCommand = new AddGroupCommand(CommandType.NEW_GROUP, new String[]{GROUP_NAME}, SESSION_ID);
        assertEquals(GROUP_NAME, addGroupCommand.getGroupName(),"Should return the group name");
    }

    @Test
    void testValidateWithMoreArguments() {
        AddGroupCommand addGroupCommand = new AddGroupCommand(CommandType.NEW_GROUP, new String[]{GROUP_NAME, GROUP_NAME}, SESSION_ID);
        assertThrows(IllegalArgumentException.class, addGroupCommand::validateArguments,
                "Should throw IllegalArgumentException when there are more arguments");
    }

    @Test
    void testExecute() throws AlreadyExistingException {
        try(var staticMock = mockStatic(BookmarkManager.class)) {
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManager);

            AddGroupCommand addGroupCommand = new AddGroupCommand(CommandType.NEW_GROUP, new String[]{GROUP_NAME}, SESSION_TRUE);
            Response response = new Response("Successfully added group", true, SESSION_TRUE, CommandType.NEW_GROUP);

            assertEquals(response, addGroupCommand.execute(),"Should return a successful response");
        }
    }

    @Test
    void testExecuteWithFalse() {
        try(var staticMock = mockStatic(BookmarkManager.class)) {
            staticMock.when(BookmarkManager::getInstance).thenReturn(bookmarkManager);

            AddGroupCommand addGroupCommand = new AddGroupCommand(CommandType.NEW_GROUP, new String[]{GROUP_NAME}, SESSION_FALSE);
            Response response = new Response("Something went wrong. Try again!", false, SESSION_FALSE, CommandType.NEW_GROUP);
            assertEquals(response, addGroupCommand.execute(),"Should return a unsuccessful response");
        }
    }
}
