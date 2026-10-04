package pokhval.wordsSearcher.concurrency;

import pokhval.wordsSearcher.util.PositionTracker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

/**
 * The great text divider divides file into blocks of chars.
 * It also repeats last blocks chars where the longest word might be hidden.
 * Because of repeating some chars, there will be no lost word matches, some words will be
 * found more times, then they should have been. For that reason, duplicates will be removed afterward.
 */
public class TextDivider {
    /**
     * divides file into
     * @param filePath path to file from CLI line :)
     * @param chunkSize amount of characters we would like to read
     * @param maxWordLength size of overlap that must be in order not to lose words
     *                      as we divide randomly
     * @param chunkProcessor a callback function dedicated to work with read chunk
     *                       in order to work with huge files weighting gigabytes
     * returns nothing as it provides text chunks straight to callback function
     * @throws Exception mostly IOException, as it works with file
     */
    public static void divideAndProcess(
            String filePath,
            int chunkSize,
            int maxWordLength,
            BiConsumer<String, PositionTracker> chunkProcessor
    ) throws Exception {
        int overlapSize = maxWordLength - 1;
        int globalCharIndex = -1;
        int globalCharInLineIndex = -1;
        int globalLineIndex = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            char[] mainBuffer = new char[chunkSize];
            char[] overlapBuffer = new char[overlapSize];

            while (true) {
                int charsRead = reader.read(mainBuffer, 0, chunkSize);
                // BufferedReader doesn't read same amounts of chars each time
                if (charsRead == -1) {
                    break;
                }

                StringBuilder chunkBuilder = new StringBuilder();
                chunkBuilder.append(mainBuffer, 0, charsRead);

                reader.mark(overlapSize + 1);
                int overlapRead = reader.read(overlapBuffer, 0, overlapSize);

                if (overlapRead != -1) {
                    chunkBuilder.append(overlapBuffer, 0, overlapRead);
                    reader.reset();
                }

                PositionTracker pt = new PositionTracker(globalLineIndex, globalCharInLineIndex, globalCharIndex);
                chunkProcessor.accept(chunkBuilder.toString(), pt);

                for (int i = 0; i < charsRead; i++) {
                    if (mainBuffer[i] == '\n') {
                        globalLineIndex++;
                        globalCharInLineIndex = -1;
                    } else {
                        globalCharInLineIndex++;
                    }
                }

                // overlap is not present as next chunk has them again
                globalCharIndex += charsRead;
            }
        }
    }
}
