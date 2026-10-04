package pokhval.wordsSearcher.searchAlgorithms.KnuthMorrisPratt;

import pokhval.wordsSearcher.searchAlgorithms.KMPSearcher;
import pokhval.wordsSearcher.util.CharProcessor;
import pokhval.wordsSearcher.util.PositionTracker;
import pokhval.wordsSearcher.util.WordMatch;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KMPSearcherTest {
    @Test
    public void testOneLetterWord() {
        String sample = "I AA some kind of a A sentence";
        KMPSearcher kmpSearcher = new KMPSearcher(
                "A",
                new PositionTracker(0, -1, -1));
        List<WordMatch> result = kmpSearcher.search(
                sample,
                new CharProcessor(true)
        );

        List<WordMatch> expected = new ArrayList<>();
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

        assertEquals(expected, result);
    }

    @Test
    public void testOneWordOccurrenceSentence() {
        String sample = "I am some kind of a barbara sentence";
        KMPSearcher kmpSearcher = new KMPSearcher(
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
        KMPSearcher kmpSearcher = new KMPSearcher(
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
