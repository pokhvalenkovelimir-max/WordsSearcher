package pokhval.wordsSearcher.searchAlgorithms.RabinKarp;

import pokhval.wordsSearcher.searchAlgorithms.RabinKarpSearcher;
import pokhval.wordsSearcher.util.CharProcessor;
import pokhval.wordsSearcher.util.PositionTracker;
import pokhval.wordsSearcher.util.WordMatch;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RabinKarpSearcherTest {
    @Test
    public void testOneLetterWord() {
        String sample = "A AA some kind of a A sentence";
        RabinKarpSearcher kmpSearcher = new RabinKarpSearcher(
                "A",
                new PositionTracker(0, -1, -1));
        List<WordMatch> result = kmpSearcher.search(
                sample,
                new CharProcessor(true)
        );

        List<WordMatch> expected = getWordMatches();

        assertEquals(expected, result);
    }

    private static List<WordMatch> getWordMatches() {
        List<WordMatch> expected = new ArrayList<>();
        expected.add(new WordMatch(
                "A",
                new PositionTracker(0, 0, 0)
        ));
        expected.add(new WordMatch(
                "A",
                new PositionTracker(0, 2, 2)
        ));
        expected.add(new WordMatch(
                "A",
                new PositionTracker(0, 3, 3)
        ));
        expected.add(new WordMatch(
                "A",
                new PositionTracker(0, 20, 20)
        ));
        return expected;
    }

    @Test
    public void testOneWordOccurrenceSentence() {
        String sample = "I am some kind of a barbara sentence";
        RabinKarpSearcher kmpSearcher = new RabinKarpSearcher(
                "barbara",
                new PositionTracker(0, -1, -1));
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
    public void testSameMultipleLines() {
        String sample = "I am some kind of a sample sentence\n\nI am some kind of a sample sentence\nI am some kind of a sample sentence";
        RabinKarpSearcher kmpSearcher = new RabinKarpSearcher(
                "sample",
                new PositionTracker(0, -1, -1)
        );
        List<WordMatch> result = kmpSearcher.search(
                sample,
                new CharProcessor(true)
        );

        List<WordMatch> expected = new ArrayList<>();
        expected.add(new WordMatch(
                "sample",
                new PositionTracker(0, 20, 20)
        ));
        expected.add(new WordMatch(
                "sample",
                new PositionTracker(2, 20, 57)
        ));
        expected.add(new WordMatch(
                "sample",
                new PositionTracker(3, 20, 93)
        ));

        assertEquals(expected, result);
    }
}
