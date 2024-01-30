package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.chrome;

import bg.sofia.uni.fmi.mjt.project.bookmarks.models.Bookmark;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.Extractor;

import java.util.List;

public interface BookmarksExtractor extends Extractor {
    List<Bookmark> extract();
}
