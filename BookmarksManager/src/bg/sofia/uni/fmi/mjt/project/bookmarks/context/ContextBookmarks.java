package bg.sofia.uni.fmi.mjt.project.bookmarks.context;

import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Group;

import java.util.List;

public interface ContextBookmarks {
    /**
     * Creates a new group and file for the given user.
     *
     * @param username the user for which the group should be created.
     * @param group the group to be created.
     *
     */
    void addGroup(String username, Group group);

    /**
     * Returns if the given user has a group with the given name.
     *
     * @param username the user for which the group should be checked.
     * @param groupName the name of the group to be checked.
     *
     * @return true if the user has a group with the given name, false otherwise.
     */
    boolean isExistingGroup(String username, String groupName);

    /**
     * Adds a bookmark to the given group for the given user.
     *
     * @param username the user for which the bookmark should be added.
     * @param groupName the name of the group for which the bookmark should be added.
     * @param bookmark the bookmark to be added.
     *
     */
    void addBookmark(String username, String groupName, Bookmark bookmark);

    /**
     * Returns if the given user has a bookmark with the given url in the given group.
     *
     * @param username the user to be checked.
     * @param groupName the name of the group to be checked.
     * @param url the url of the bookmark to be checked.
     *
     * @return true if the user has a bookmark with the given url in the given group, false otherwise.
     */
    boolean isExistingBookmark(String username, String groupName, String url);

    /**
     * Removes the given group for the given user.
     *
     * @param username the user for which the group should be removed.
     * @param groupName the name of the group to be removed.
     *
     */
    void removeBookmark(String username, String groupName, String bookmarkUrl);

    /**
     * Gets the bookmarks from the group with the given name for the given user.
     *
     * @param username the user whose group should be returned.
     * @param groupName the name of the group to be returned.
     *
     * @return a list of all bookmarks from the given group for the given user.
     */
    List<Bookmark> getBookmarks(String username, String groupName);

    /**
     * Gets all bookmarks for the given user.
     *
     * @param username the user whose bookmarks should be returned.
     *
     * @return a list of all bookmarks for the given user.
     */
    List<Bookmark> getBookmarks(String username);

    /**
     * Searches for bookmarks for the given user by tag.
     *
     * @param username the user whose bookmarks should be searched.
     * @param strings the keywords to be searched for.
     *
     * @return a list of all bookmarks for the given user that match the given keywords.
     */
    List<Bookmark> searchByTag(String username, List<String> strings);

    /**
     * Searches for bookmarks for the given user by title.
     *
     * @param username the user whose bookmarks should be searched.
     * @param title the title to be searched for.
     *
     * @return a list of all bookmarks for the given user that match fully or partly the given title.
     */
    List<Bookmark> searchByTitle(String username, String title);

    /**
     * Removes all bookmarks that return 404.
     *
     * @param username the user whose bookmarks should be cleaned up.
     *
     */
    void cleanUp(String username);

    /**
     * Imports bookmarks from the user's Chrome browser.
     *
     * @param username the user whose bookmarks should be imported.
     *
     */
    void importFromChrome(String username);
}
