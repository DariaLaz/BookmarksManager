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
    private static final int START_INDEX = 0;
    private static final int SUFIX_LEN_2 = 2;
    private static final int SUFIX_LEN_3 = 3;
    private static final String EMPTY = "";
    private static final Set<String> IGNORE = Set.of("i", "me", "my", "myself", "we", "our", "ours", "ourselves",
            "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers",
            "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which",
            "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or",
            "because", "until", "while", "against", "between", "into", "through", "during", "before", "after", "above",
            "below", "up", "down", "in", "out", "off", "over", "under", "again", "further", "then", "once", "here",
            "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other",
            "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can",
            "will", "just", "don", "should", "now", "of", "to", "for", "with", "as", "at", "from", "by", "on", "about");

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
        String[] toReplace = {".", ",", "!", "?", ")", "]", "}", "(", "[", "{", ":", ";", "-", "\""};

        for (String s : toReplace) {
            word = word.replace(s, EMPTY);
        }

        if (word.endsWith("ly") || word.endsWith("ed") || word.endsWith("es")) {
            word = word.substring(START_INDEX, word.length() - SUFIX_LEN_2).toLowerCase();
        }
        if (word.endsWith("ing")) {
            word = word.substring(START_INDEX, word.length() - SUFIX_LEN_3).toLowerCase();
        }

        return word.toLowerCase();
    }
}
