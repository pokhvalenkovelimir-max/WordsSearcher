package pokhval.wordsSearcher;

import pokhval.wordsSearcher.concurrency.ParallelSearchCoordinator;
import pokhval.wordsSearcher.searchAlgorithms.ACSearcher;
import pokhval.wordsSearcher.searchAlgorithms.TextSearcher;
import pokhval.wordsSearcher.util.PositionTracker;
import pokhval.wordsSearcher.util.WordMatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
    // since it is very hard to write tests manually, I will use built-in function from Java
    private List<Integer> getExpectedAbsolutePositions(String fullText, String keyword) {
        List<Integer> expectedPositions = new ArrayList<>();
        int index = fullText.indexOf(keyword);

        while (index >= 0) {
            expectedPositions.add(index);
            index = fullText.indexOf(keyword, index + 1); // +1 correctly catches overlaps!
        }

        return expectedPositions;
    }

    @Test
    void testAhoCorasickAgainstJavaNative(@TempDir Path tempDir) throws Exception {
        String keyword = "arab";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50000; i++) {
            // "arab" appears occasionally, sometimes overlapping like "arabarab"
            sb.append("some random text arab more text arabarab even more text\n");
        }
        String fullText = sb.toString();

        Path tempFile = tempDir.resolve("massive_test.txt");
        Files.writeString(tempFile, fullText);

        List<Integer> expectedPositions = getExpectedAbsolutePositions(fullText, keyword);

        List<String> keywords = List.of(keyword);
        int chunkSize = 50_000; // it's not 5_000_000 like in main function
        int maxWordLength = keyword.length();

        Function<PositionTracker, TextSearcher> factory =
                (tracker) -> new ACSearcher(keywords, tracker);

        ParallelSearchCoordinator coordinator = new ParallelSearchCoordinator(true);

        List<WordMatch> actualMatches = coordinator.executeSearch(
                tempFile.toAbsolutePath().toString(),
                chunkSize,
                maxWordLength,
                factory
        );

        List<Integer> actualPositions = actualMatches.stream()
                .map(match -> match.position().fromFileStartCharIndex())
                .toList();

        assertEquals(expectedPositions.size(), actualPositions.size(), "Amount of found Matches fail");
        assertEquals(expectedPositions, actualPositions, "Absolute positions don't match");
    }
}
