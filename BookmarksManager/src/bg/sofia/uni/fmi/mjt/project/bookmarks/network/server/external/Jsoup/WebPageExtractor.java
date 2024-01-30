package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.external.Jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebPageExtractor implements PageExtractor {
    Document doc;
    private static final int COUNT = 10;
    private static final Set<String> IGNORE = Set.of("", "a", "an", "and", "are", "as", "at", "be", "by",
            "for", "from", "has", "he", "in", "is", "it", "its", "of", "on", "that", "the", "to", "was", "were",
            "will", "with",  "www", "com", "org", "net", "info", "edu", "gov", "mil", "int", "co", "uk",
            "...", ",", ".", "!", "?", "(", ")", "[", "]", "{", "}", "-", "–", "—", ":", ";", "\"", "'", "’", "“");
    public WebPageExtractor(String url) throws IOException {
        this.doc = Jsoup.connect(url).get();
    }

    @Override
    public String title() {
        return doc.title();
    }

    @Override
    public List<String> keywords() {
        String text = doc.body().text();
        return getKeywords(text);
    }

    private List<String> getKeywords(String text) {
        String[] words = text.split(" ");
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            String newWord = getWord(word);
            if (IGNORE.contains(newWord)) {
                continue;
            }
            if (!wordCount.containsKey(newWord)) {
                wordCount.put(newWord, 0);
            }
            wordCount.put(newWord, wordCount.get(newWord) + 1);
        }
        return wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(COUNT)
                .map(Map.Entry::getKey)
                .toList();
    }

    private String getWord(String word) {
        //TODO refactor
        word = word.replace(".", "");
        if (word.endsWith(",") || word.endsWith(".") || word.endsWith("!")
                || word.endsWith("?") || word.endsWith(")") || word.endsWith("]") || word.endsWith("}"))
            word = word.substring(0, word.length() - 1).toLowerCase();
        if (word.startsWith("\"") || word.startsWith("'") || word.startsWith("“")
                || word.startsWith("(") || word.startsWith("[") || word.startsWith("{"))
            word = word.substring(1).toLowerCase();
        if (word.endsWith("ly") || word.endsWith("ed") || word.endsWith("es"))
            word = word.substring(0, word.length() - 2).toLowerCase();
        if (word.endsWith("ing"))
            word = word.substring(0, word.length() - 3).toLowerCase();
        return word.toLowerCase();
    }
}
