package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.session;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class SessionManagerTest {
//    @Test
//    void testLoginWithCorrectUser(){
//        SessionManager sessionManager = mock(SessionManager.class);
//        Mockito.when(sessionManager.getUser("daria")).thenReturn(new User("daria", PasswordHash.hash("123")));
//
//        assertFalse(sessionManager.login("daria", "123"));
//    }

//    @Test
//    void testLoginWithWrongPassword(){
//        SessionManager sessionManager = mock(SessionManager.class);
//        Mockito.when(sessionManager.getUser("daria")).thenReturn(new User("daria", "123"));
//
//        assertFalse(sessionManager.login("daria", "1234"));
//    }
//
//    @Test
//    void testLoginWithAlreadyLoggedInUser(){
//        SessionManager sessionManager = mock(SessionManager.class);
//        Mockito.when(sessionManager.getActiveSessions()).thenReturn(Map.of("daria", "123"));
//        assertThrows(AlreadyExistingUser.class, () -> sessionManager.login("daria", "123"));
//    }
//
//    @Test
//    void testLogoutWithNullSessionId(){
//        SessionHandler sessionHandler = SessionManager.getInstance();
//        assertThrows(IllegalArgumentException.class, () -> sessionHandler.logout(null));
//    }
//
//    @Test
//    void testLogoutWithCorrectSessionId(){
//        SessionManager sessionHandler = Mockito.mock(SessionManager.class);
//        Mockito.when(sessionHandler.getActiveSessions()).thenReturn(Map.of("daria", "123"));
//        doNothing().when(sessionHandler).removeFromActiveSessions("daria");
//        Mockito.when(sessionHandler.logout("123")).thenReturn(true);
//    }
//
//    @Test
//    void testLogoutWithNonExistingSessionId(){
//        SessionManager sessionHandler = Mockito.mock(SessionManager.class);
//        Mockito.when(sessionHandler.getActiveSessions()).thenReturn(Map.of("daria", "123"));
//        doNothing().when(sessionHandler).removeFromActiveSessions("daria");
//        Mockito.when(sessionHandler.logout("1234")).thenReturn(true);
//    }
//
//    @Test
//    void testRegisterWithNullUser(){
//
//    }
//
//    @Test
//    void testRegisterWithCorrectUser() {
//        SessionManager sessionManager = mock(SessionManager.class);
//        Mockito.when(sessionManager.getUser("daria")).thenReturn(null);
//        doNothing().when(sessionManager).addNewUser("daria", "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=");
//        assertTrue(sessionManager.register("daria", "123"));
//    }
//
//    @Test
//    void testRegisterWithAlreadyExistingUser(){
//        SessionManager sessionManager = mock(SessionManager.class);
//        Mockito.when(sessionManager.getUser("daria")).thenReturn(new User("daria", "123"));
//        doNothing().when(sessionManager).addNewUser("daria", "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=");
//        assertFalse(sessionManager.register("daria", "123"));
//    }


}
