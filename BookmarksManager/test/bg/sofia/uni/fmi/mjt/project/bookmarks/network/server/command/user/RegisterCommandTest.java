package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class RegisterCommandTest {
    private static final String SESSION_ID = "sessionID";
    private static SessionManager sessionHandler;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String WRONG_USERNAME = "wrongUsername";

    @BeforeAll
    static void setUp() throws AlreadyExistingException {
        sessionHandler = mock(SessionManager.class);
        when(sessionHandler.register(WRONG_USERNAME, PASSWORD))
                .thenReturn(false);
        when(sessionHandler.register(USERNAME, PASSWORD))
                .thenReturn(true);
    }

    @Test
    public void testExecuteSuccessful() {
        try (var sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getInstance).thenReturn(sessionHandler);

            Response response = new Response("Successfully registered",true, null, CommandType.REGISTER);
            RegisterCommand registerCommand = new RegisterCommand(CommandType.REGISTER, new String[]{USERNAME, PASSWORD}, SESSION_ID);
            assertEquals(response, registerCommand.execute(), "Should return a successful response");
        }
    }

    @Test
    public void testExecuteUnsuccessful() {
        try (var sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getInstance).thenReturn(sessionHandler);

            Response response = new Response("Something went wrong. Try again!",false, null, CommandType.REGISTER);
            RegisterCommand registerCommand = new RegisterCommand(CommandType.REGISTER, new String[]{WRONG_USERNAME, PASSWORD}, SESSION_ID);
            assertEquals(response, registerCommand.execute(), "Should return an unsuccessful response");
        }
    }
}
