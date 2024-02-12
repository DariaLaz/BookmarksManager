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

public class LoginCommandTest {
    private static final String SESSION_ID = "sessionID";
    private static SessionManager sessionHandler;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String WRONG_USERNAME = "wrongUsername";

    @BeforeAll
    static void setUp() throws AlreadyExistingException {
        sessionHandler = mock(SessionManager.class);
        when(sessionHandler.login(WRONG_USERNAME, PASSWORD))
                .thenReturn(false);
        when(sessionHandler.login(USERNAME, PASSWORD))
                .thenReturn(true);
    }
    @Test
    public void testExecuteUnsuccessful() {
        try (var sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getInstance).thenReturn(sessionHandler);

            Response response = new Response("Wrong username or password", false, SESSION_ID, CommandType.LOGIN);
            LoginCommand loginCommand = new LoginCommand(CommandType.LOGIN, new String[]{WRONG_USERNAME, PASSWORD}, SESSION_ID);
            assertEquals(response, loginCommand.execute(), "Should return an unsuccessful response");
        }
    }

    @Test
    public void testExecuteSuccessful() {
        try (var sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getInstance).thenReturn(sessionHandler);

            Response response = new Response("Successfully logged in", true, SESSION_ID, CommandType.LOGIN);
            LoginCommand loginCommand = new LoginCommand(CommandType.LOGIN, new String[]{USERNAME, PASSWORD}, SESSION_ID);
            assertEquals(response, loginCommand.execute(), "Should return a successful response");
        }
    }
}
