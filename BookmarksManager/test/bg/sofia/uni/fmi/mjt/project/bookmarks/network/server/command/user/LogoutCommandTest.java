package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.user;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AlreadyExistingException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.Response;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionHandler;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.handlers.sessions.SessionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class LogoutCommandTest {
    private static final String SESSION_FALSE = "sessionFalse";
    private static final String SESSION_TRUE = "sessionTrue";
    private static SessionManager sessionHandler;

    @BeforeAll
    static void setUp() {
        sessionHandler = mock(SessionManager.class);
        when(sessionHandler.logout(SESSION_TRUE))
                .thenReturn(true);

        when(sessionHandler.logout(SESSION_FALSE))
                .thenReturn(false);
    }

    @Test
    void testLogoutUnsuccessfully() {
        try (var sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getInstance).thenReturn(sessionHandler);
            LogoutCommand logoutCommand = new LogoutCommand(CommandType.LOGOUT, new String[]{}, SESSION_FALSE);

            Response response = new Response("Something went wrong. Try again!",false, null, CommandType.LOGOUT);
            assertEquals(response, logoutCommand.execute(), "Should return an unsuccessful response");
        }
    }
    @Test
    void testLogoutSccessfully() {
        try (var sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getInstance).thenReturn(sessionHandler);

            LogoutCommand logoutCommand = new LogoutCommand(CommandType.LOGOUT, new String[]{}, SESSION_TRUE);
            Response response = new Response("Successfully logged out", true, null, CommandType.LOGOUT);
            assertEquals(response, logoutCommand.execute(), "Should return a successful response");
        }
    }
}
