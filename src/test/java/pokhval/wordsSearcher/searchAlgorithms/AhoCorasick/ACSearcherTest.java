package pokhval.wordsSearcher.searchAlgorithms.AhoCorasick;

import pokhval.wordsSearcher.searchAlgorithms.ACSearcher;
import pokhval.wordsSearcher.util.CharProcessor;
import pokhval.wordsSearcher.util.PositionTracker;
import pokhval.wordsSearcher.util.WordMatch;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ACSearcherTest {
    @Test
    public void testBasicOneWord() {
        String sample = "I am some kind of a barbara sentence";
        ArrayList<String> searchedWords = new ArrayList<>();
        searchedWords.add("barbara");

        ACSearcher kmpSearcher = new ACSearcher(
                searchedWords,
                new PositionTracker(0, -1, -1)
        );
        List<WordMatch> result = kmpSearcher.search(
                sample,
                new CharProcessor(true)
        );

        List<WordMatch> expected = new ArrayList<>();
        expected.add(new WordMatch(
                "barbara",
                new PositionTracker(0, 20, 20)
        ));

        assertEquals(expected, result);
    }

    @Test
    public void testMultipleWordsOnDifferentLines() {
        // Line 0 (Length: 31): "THE ARAB TRADED AT THE BAZAAR\n" -> Includes "ARA", "ARAB", "BAR"
        // Line 1 (Length: 17): "AN ARABIAN NIGHT\n"              -> Includes "ARA", "ARAB"
        // Line 2 (Length: 26): "WELCOME TO BARABARA LAND!\n"    -> Includes "BAR", "ARA", "BARABA", "ARAB"
        // Line 3 (Length: 17): "DEAR BARBARA..."                 -> Includes "BAR", "BARBARA", "ARA"
        String sample = "THE ARAB TRADED AT THE BAZAAR\nAN ARABIAN NIGHT\nWELCOME TO BARABARA LAND!\nDEAR BARBARA...";

        ArrayList<String> searchedWords = new ArrayList<>();
        searchedWords.add("ARA");
        searchedWords.add("ARAB");
        searchedWords.add("BAR");
        searchedWords.add("BARABA");
        searchedWords.add("BARBARA");

        ACSearcher acSearcher = new ACSearcher(
                searchedWords,
                new PositionTracker(0, -1, -1)
        );
        List<WordMatch> result = acSearcher.search(
                sample,
                new CharProcessor(true)
        );

        List<WordMatch> expected = new ArrayList<>();

        // === LINE 0 ===
        expected.add(new WordMatch("ARA", new PositionTracker(0, 4, 4)));
        expected.add(new WordMatch("ARAB", new PositionTracker(0, 4, 4)));

        // === LINE 1 ===
        expected.add(new WordMatch("ARA", new PositionTracker(1, 3, 33)));
        expected.add(new WordMatch("ARAB", new PositionTracker(1, 3, 33)));

        // === LINE 2 ===
        expected.add(new WordMatch("BAR", new PositionTracker(2, 11, 58)));
        expected.add(new WordMatch("ARA", new PositionTracker(2, 12, 59)));
        expected.add(new WordMatch("ARAB", new PositionTracker(2, 12, 59)));
        expected.add(new WordMatch("BARABA", new PositionTracker(2, 11, 58)));
        expected.add(new WordMatch("BAR", new PositionTracker(2, 15, 62))); // The hidden BAR!
        expected.add(new WordMatch("ARA", new PositionTracker(2, 16, 63))); // The hidden ARA!

        // === LINE 3 ===
        expected.add(new WordMatch("BAR", new PositionTracker(3, 5, 78)));
        expected.add(new WordMatch("BAR", new PositionTracker(3, 8, 81)));
        expected.add(new WordMatch("BARBARA", new PositionTracker(3, 5, 78)));
        expected.add(new WordMatch("ARA", new PositionTracker(3, 9, 82)));

        assertEquals(expected, result);
    }
}
