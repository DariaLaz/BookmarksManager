package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.context.file.FileManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.file.GroupsFileManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.context.file.UserFileManager;
import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AuthException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.User;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.security.PasswordHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

public class ContextDataTest {

    private static final String USER_NAME = "name";
    private static final String USER_PASS = "password";
    private static final String USER_HASH_PASS = new PasswordHash().hash("password");
    private static final String USER_ID = "id";
    private static final String USER_RANDOM_NAME = "random";
    private static final String GROUP_NAME = "group";
    private Context contextData;
    private final User user = new User(USER_NAME, USER_HASH_PASS, USER_ID);
    @BeforeEach
    void setUp() {
        contextData = ContextData.getInstance();
        contextData.cachedRegisteredUsers().put(USER_NAME, user);
        contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());
        contextData.cachedBookmarkGroups().get(USER_NAME).add(new Group(GROUP_NAME));
    }

    @Test
    void testGetUserWithCachedData() {
        contextData.cachedRegisteredUsers().put(USER_NAME, user);
        assertEquals(user, ((ContextData)contextData).getUser(USER_NAME),"Should return the cached user with the given username");
    }
    @Test
    void testGetUserWithoutCachedData() {
        try (MockedStatic<UserFileManager> ufm = Mockito.mockStatic(UserFileManager.class)) {
            ufm.when(() -> UserFileManager.load(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap()))
                    .then(invocation -> {
                        Map<String, User> cachedRegisteredUsers = invocation.getArgument(1);
                        Map<String, ArrayList<Group>> cachedBookmarkGroups = invocation.getArgument(2);
                        cachedRegisteredUsers.put(USER_NAME, user);
                        cachedBookmarkGroups.put(USER_NAME, new ArrayList<>());
                        return null;
                    });

            assertEquals(user, ((ContextData)contextData).getUser(USER_NAME),"Should return the user with the given username");
        }
    }

    @Test
    void testGetUserWithNotExistingUser() {
        try (MockedStatic<UserFileManager> ufm = mockStatic(UserFileManager.class)) {
            ufm.when(() -> UserFileManager.load(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap()))
                    .then(invocation -> null);

            assertNull(((ContextData)contextData).getUser(USER_RANDOM_NAME),"Should return null with not existing user");
        }
    }

    @Test
    void testAddNewUserWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.addNewUser(null, USER_HASH_PASS),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.addNewUser("", USER_HASH_PASS),
                "Should throw IllegalArgumentException with null username");
    }

    @Test
    void testAddNewUserWithNullOrEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> contextData.addNewUser(USER_NAME, null),
                "Should throw IllegalArgumentException with null password");
        assertThrows(IllegalArgumentException.class, () -> contextData.addNewUser(USER_NAME, ""),
                "Should throw IllegalArgumentException with null password");
    }

    @Test
    void testAddNewUserWithCorrectData() {
        try (MockedStatic<UserFileManager> ufm = mockStatic(UserFileManager.class)) {
            ufm.when(() -> UserFileManager.addNew(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                            Mockito.anyMap(), Mockito.anyMap(), Mockito.any(User.class)))
                    .then(invocation ->  null);
            assertDoesNotThrow(() -> contextData.addNewUser(USER_NAME, USER_HASH_PASS),
                    "Should not throw with correct data");
        }
    }

    @Test
    void testAddNewUserWithIncorrectData() {
        try (MockedStatic<UserFileManager> ufm = mockStatic(UserFileManager.class)) {
            ufm.when(() -> UserFileManager.addNew(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                            Mockito.anyMap(), Mockito.anyMap(), Mockito.any(User.class)))
                    .then(invocation ->  null);
            assertThrows(IllegalArgumentException.class, () -> contextData.addNewUser("", ""),
                    "Should throw IllegalArgumentException with null or empty arguments");
        }
    }
    @Test
    void testIsCorrectPasswordWithNotRegistered() {
        try (MockedStatic<UserFileManager> ufm = mockStatic(UserFileManager.class)) {
            ufm.when(() -> UserFileManager.load(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap()))
                    .then(invocation -> null);

            Context contextData = ContextData.getInstance();

            assertFalse(contextData.isCorrectPassword(USER_RANDOM_NAME, USER_PASS),
                    "Should return false then the user is not registered");
        }
    }

    @Test
    void testIsCorrectPasswordWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.isCorrectPassword(null, USER_HASH_PASS),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.isCorrectPassword("", USER_HASH_PASS),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testIsCorrectPasswordWithNullOrEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> contextData.isCorrectPassword(USER_NAME, null),
                "Should throw IllegalArgumentException with null password");
        assertThrows(IllegalArgumentException.class, () -> contextData.isCorrectPassword(USER_NAME, ""),
                "Should throw IllegalArgumentException with empty password");
    }

    @Test
    void testIsCorrectPasswordWithCorrectData() {
        assertTrue(contextData.isCorrectPassword(USER_NAME, USER_PASS),
                "Should return true with correct data");
    }

    @Test
    void testAddGroupWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.addGroup(null, new Group(GROUP_NAME)),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.addGroup("", new Group(GROUP_NAME)),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testAddGroupWithCorrectArguments() {
        try (MockedStatic<FileManager> ufm = mockStatic(FileManager.class)) {
            ufm.when(() -> FileManager.create(Mockito.anyString()))
                    .then(invocation -> null);
            Group group = new Group(GROUP_NAME);
            contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());
            assertDoesNotThrow(() -> contextData.addGroup(USER_NAME, group),"Should not throw with correct arguments");
        }
    }

    @Test
    void testAddGroup() {
        try (MockedStatic<FileManager> ufm = mockStatic(FileManager.class)) {
            ufm.when(() -> FileManager.create(Mockito.anyString()))
                    .then(invocation -> null);
            Group group = new Group(GROUP_NAME);
            contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());

            contextData.addGroup(USER_NAME, group);
            assertTrue(contextData.cachedBookmarkGroups().get(USER_NAME).contains(group),
                    "Should add the group to the user's groups");
        }
    }

    @Test
    void testIsExistingGroupWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingGroup(null, GROUP_NAME),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingGroup("", GROUP_NAME),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testIsExistingGroupWithNullOrEmptyGroupName() {
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingGroup(USER_NAME, null),
                "Should throw IllegalArgumentException with null group name");
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingGroup(USER_NAME, ""),
                "Should throw IllegalArgumentException with empty group name");
    }

    @Test
    void testIsExistingGroupWithNotExistingGroup() {
        try (MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)) {
            cfm.when(() -> GroupsFileManager.updateGroups(Mockito.anyString(), Mockito.anyList()))
                    .then(invocation -> null);

            contextData.cachedRegisteredUsers().put(USER_NAME, new User(USER_NAME, USER_HASH_PASS, USER_ID));
            contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());

            assertFalse(contextData.isExistingGroup(USER_NAME, GROUP_NAME),
                    "Should return false with not existing group");
        }
    }

    @Test
    void testIsExistingGroupWithExistingGroup() {
        try (MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)) {
            cfm.when(() -> GroupsFileManager.updateGroups(Mockito.anyString(), Mockito.anyList()))
                    .then(invocation -> null);

            contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());
            contextData.cachedBookmarkGroups().get(USER_NAME).add(new Group(GROUP_NAME));

            assertTrue(contextData.isExistingGroup(USER_NAME, GROUP_NAME),"Should return true with existing group");
        }
    }

    @Test
    void testAddBookmarkWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.addBookmark(null, GROUP_NAME, null),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.addBookmark("", GROUP_NAME, null),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testAddBookmarkWithNullOrEmptyGroupName() {
        assertThrows(IllegalArgumentException.class, () -> contextData.addBookmark(USER_NAME, null, null),
                "Should throw IllegalArgumentException with null group name");
        assertThrows(IllegalArgumentException.class, () -> contextData.addBookmark(USER_NAME, "", null),
                "Should throw IllegalArgumentException with empty group name");
    }

    @Test
    void testAddBookmarkWithNotRegistratedUser() {
        try (MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)) {
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            assertThrows(AuthException.class, () -> contextData.addBookmark(USER_RANDOM_NAME, GROUP_NAME, null),
                    "Should throw IllegalArgumentException with not registrated user");
        }
    }

    @Test
    void testAddBookmarkWithNotExistingGroup() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            contextData.cachedRegisteredUsers().put(USER_NAME, new User(USER_NAME, USER_HASH_PASS, USER_ID));
            contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());

            assertThrows(IllegalArgumentException.class, () -> contextData.addBookmark(USER_NAME, USER_RANDOM_NAME, null),
                    "Should throw IllegalArgumentException with not existing group");
        }
    }

    @Test
    void testAddBookmarkWithCorrectData() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);
            cfm.when(() -> GroupsFileManager.addNewBookmark(Mockito.anyString(), Mockito.anyString()))
                    .then(invocation -> null);

            contextData.cachedRegisteredUsers().put(USER_NAME, new User(USER_NAME, USER_HASH_PASS, USER_ID));
            contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());
            contextData.cachedBookmarkGroups().get(USER_NAME).add(new Group(GROUP_NAME));
            Bookmark bookmark = new Bookmark("url", "title", List.of("tag"));
            contextData.addBookmark(USER_NAME, GROUP_NAME, bookmark);

            assertTrue(contextData.cachedBookmarkGroups().get(USER_NAME).get(0).getBookmarks().contains(bookmark),
                    "Should add the bookmark to the group");
        }
    }

    @Test
    void testIsExistingBookmarkWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingBookmark(null, GROUP_NAME, "url"),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingBookmark("", GROUP_NAME, "url"),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testIsExistingBookmarkWithNullOrEmptyGroupName() {
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingBookmark(USER_NAME, null, "url"),
                "Should throw IllegalArgumentException with null group name");
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingBookmark(USER_NAME, "", "url"),
                "Should throw IllegalArgumentException with empty group name");
    }

    @Test
    void testIsExistingBookmarkWithNullOrEmptyUrl() {
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingBookmark(USER_NAME, GROUP_NAME, null),
                "Should throw IllegalArgumentException with null url");
        assertThrows(IllegalArgumentException.class, () -> contextData.isExistingBookmark(USER_NAME, GROUP_NAME, ""),
                "Should throw IllegalArgumentException with empty url");
    }

    @Test
    void testIsExistingBookmarkWithNotRegistratedUser() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            assertThrows(AuthException.class, () -> contextData.isExistingBookmark(USER_RANDOM_NAME, GROUP_NAME, "url"),
                    "Should throw IllegalArgumentException with not registrated user");
        }
    }

    @Test
    void testIsExistingBookmarkWithNotExistingGroup() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            contextData.cachedRegisteredUsers().put(USER_NAME, new User(USER_NAME, USER_HASH_PASS, USER_ID));
            contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());

            assertThrows(IllegalArgumentException.class, () -> contextData.isExistingBookmark(USER_NAME, GROUP_NAME, "url"),
                    "Should throw IllegalArgumentException with not existing group");
        }
    }

    @Test
    void testRemoveBookmarkWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.removeBookmark(null, GROUP_NAME, "url"),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.removeBookmark("", GROUP_NAME, "url"),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testRemoveBookmarkWithNullOrEmptyGroupName() {
        assertThrows(IllegalArgumentException.class, () -> contextData.removeBookmark(USER_NAME, null, "url"),
                "Should throw IllegalArgumentException with null group name");
        assertThrows(IllegalArgumentException.class, () -> contextData.removeBookmark(USER_NAME, "", "url"),
                "Should throw IllegalArgumentException with empty group name");
    }

    @Test
    void testRemoveBookmarkWithNullOrEmptyUrl() {
        assertThrows(IllegalArgumentException.class, () -> contextData.removeBookmark(USER_NAME, GROUP_NAME, null),
                "Should throw IllegalArgumentException with null url");
        assertThrows(IllegalArgumentException.class, () -> contextData.removeBookmark(USER_NAME, GROUP_NAME, ""),
                "Should throw IllegalArgumentException with empty url");
    }

    @Test
    void testRemoveBookmarkWithNotRegistratedUser() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            assertThrows(AuthException.class, () -> contextData.removeBookmark(USER_RANDOM_NAME, GROUP_NAME, "url"),
                    "Should throw IllegalArgumentException with not registrated user");
        }
    }

    @Test
    void testRemoveBookmarkWithNotExistingGroup() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            contextData.cachedRegisteredUsers().put(USER_NAME, new User(USER_NAME, USER_HASH_PASS, USER_ID));
            contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());

            assertThrows(IllegalArgumentException.class, () -> contextData.removeBookmark(USER_NAME, USER_RANDOM_NAME, "url"),
                    "Should throw IllegalArgumentException with not existing group");
        }
    }

    @Test
    void testRemoveBookmarkWithCorrectData() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);
            cfm.when(() -> GroupsFileManager.removeBookmark(Mockito.anyString(), Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            Bookmark bookmark = new Bookmark("url", "title", List.of("tag"));
            contextData.cachedBookmarkGroups().get(USER_NAME).add(new Group("toRemove"));
            contextData.cachedBookmarkGroups().get(USER_NAME).get(1).addBookmark(bookmark);
            contextData.removeBookmark(USER_NAME, "toRemove", "url");

            assertFalse(contextData.cachedBookmarkGroups().get(USER_NAME).get(0).getBookmarks().contains(bookmark),
                    "Should remove the bookmark from the group");
        }
    }

    @Test
    void testGetBookmarksWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.getBookmarks(null, GROUP_NAME),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.getBookmarks("", GROUP_NAME),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testGetBookmarksWithNullOrEmptyGroupName() {
        assertThrows(IllegalArgumentException.class, () -> contextData.getBookmarks(USER_NAME, null),
                "Should throw IllegalArgumentException with null group name");
        assertThrows(IllegalArgumentException.class, () -> contextData.getBookmarks(USER_NAME, ""),
                "Should throw IllegalArgumentException with empty group name");
    }

    @Test
    void testGetBookmarksWithNotRegistratedUser() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            assertThrows(AuthException.class, () -> contextData.getBookmarks(USER_RANDOM_NAME, GROUP_NAME),
                    "Should throw IllegalArgumentException with not registrated user");
        }
    }

    @Test
    void testGetBookmarksWithNotExistingGroup() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            contextData.cachedRegisteredUsers().put(USER_NAME, new User(USER_NAME, USER_HASH_PASS, USER_ID));
            contextData.cachedBookmarkGroups().put(USER_NAME, new ArrayList<>());

            assertThrows(IllegalArgumentException.class, () -> contextData.getBookmarks(USER_NAME, GROUP_NAME),
                    "Should throw IllegalArgumentException with not existing group");
        }
    }

    @Test
    void testGetBookmarksWithCorrectData() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            List<Bookmark> bookmarks = List.of(new Bookmark("url", "title", List.of("tag")));
            contextData.cachedBookmarkGroups().get(USER_NAME).get(0).addBookmark(bookmarks.get(0));

            assertEquals(bookmarks, contextData.getBookmarks(USER_NAME, GROUP_NAME),
                    "Should return the bookmarks from the group");
        }
    }

    @Test
    void testGetBookmarksWithoutGroupWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.getBookmarks(null),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.getBookmarks(""),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testGetBookmarksWithoutGroupWithNotRegistratedUser() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroups(Mockito.anyString(), Mockito.anyList()))
                    .then(invocation -> null);

            assertThrows(AuthException.class, () -> contextData.getBookmarks(USER_RANDOM_NAME),
                    "Should throw IllegalArgumentException with not registrated user");
        }
    }

    @Test
    void testGetBookmarksWithoutGroupWithCorrectData() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            assertEquals(new ArrayList<>(), contextData.getBookmarks(USER_NAME, GROUP_NAME),"Should return an empty list");
        }
    }

    @Test
    void testSearchByTagWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.searchByTag(null, List.of("tag")),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.searchByTag("", List.of("tag")),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testSearchByTagWithNotRegistratedUser() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroups(Mockito.anyString(), Mockito.anyList()))
                    .then(invocation -> null);

            assertThrows(AuthException.class, () -> contextData.searchByTag(USER_RANDOM_NAME, List.of("tag")),
                    "Should throw IllegalArgumentException with not registrated user");
        }
    }

    @Test
    void testSearchByTagWithCorrectData() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            List<Bookmark> bookmarks = List.of(new Bookmark("url", "title", List.of("tag")));
            contextData.cachedBookmarkGroups().get(USER_NAME).get(0).addBookmark(bookmarks.get(0));

            assertEquals(bookmarks, contextData.searchByTag(USER_NAME, List.of("tag")),
                    "Should return the bookmarks with the given tag");
        }
    }

    @Test
    void testSearchByTitleWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.searchByTitle(null, "title"),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.searchByTitle("", "title"),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testSearchByTitleWithNullOrEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> contextData.searchByTitle(USER_NAME, null),
                "Should throw IllegalArgumentException with null title");
        assertThrows(IllegalArgumentException.class, () -> contextData.searchByTitle(USER_NAME, ""),
                "Should throw IllegalArgumentException with empty title");
    }

    @Test
    void testSearchByTitleWithCorrectData() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroup(Mockito.anyString(), Mockito.any(Group.class)))
                    .then(invocation -> null);

            assertEquals(new ArrayList<>(), contextData.searchByTitle(USER_NAME, "title"),
                    "Should return the bookmarks with the given title");
        }
    }

    @Test
    void testCleanUpWithEmptyData() {
        assertThrows(IllegalArgumentException.class, () -> contextData.cleanUp(null),
                "Should throw IllegalArgumentException with null data");
    }

    @Test
    void testCleanUpWithNotRegistratedUser() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroups(Mockito.anyString(), Mockito.anyList()))
                    .then(invocation -> null);

            assertThrows(AuthException.class, () -> contextData.cleanUp(USER_RANDOM_NAME),
                    "Should throw IllegalArgumentException with not registrated user");
        }
    }

    @Test
    void testCleanUpWithCorrectData() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroups(Mockito.anyString(), Mockito.anyList()))
                    .then(invocation -> null);

            assertDoesNotThrow(() -> contextData.cleanUp(USER_NAME));
        }
    }

    @Test
    void testImportFromChromeWithNullOrEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> contextData.importFromChrome(null),
                "Should throw IllegalArgumentException with null username");
        assertThrows(IllegalArgumentException.class, () -> contextData.importFromChrome(""),
                "Should throw IllegalArgumentException with empty username");
    }

    @Test
    void testImportFromChromeWithNotRegistratedUser() {
        try(MockedStatic<GroupsFileManager> cfm = mockStatic(GroupsFileManager.class)){
            cfm.when(() -> GroupsFileManager.updateGroups(Mockito.anyString(), Mockito.anyList()))
                    .then(invocation -> null);

            assertThrows(AuthException.class, () -> contextData.importFromChrome(USER_RANDOM_NAME),
                    "Should throw IllegalArgumentException with not registrated user");
        }
    }

}
