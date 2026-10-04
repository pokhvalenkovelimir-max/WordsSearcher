package pokhval.wordsSearcher.util;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ResultPrinter {
    public static void printToOutputStream(List<WordMatch> results, String positionStyle, String outputFilePath) {
        //it counts with outputFilePath being null in case user wants to print to console
        try (PrintWriter writer = resolveWriter(outputFilePath)) {

            if (results.isEmpty()) {
                writer.println("No matches found.");
                return;
            }

            for (WordMatch match : results) {
                String outputLine = formatMatch(match, positionStyle);
                writer.println(outputLine);
            }

        } catch (IOException e) {
            System.err.println("Error ResultPrinter.printToFile: " + e.getMessage());
        }
    }

    private static PrintWriter resolveWriter(String outputFilePath) throws IOException {
        if (outputFilePath == null) {
            return new PrintWriter(System.out, true);
        } else {
            return new PrintWriter(new FileWriter(outputFilePath), true);
        }
    }

    private static String formatMatch(WordMatch match, String positionStyle) {
        return switch (positionStyle) {
            case "inLine" -> String.format(
                    "Word \"%s\" was found in line %d at position %d",
                    match.word(),
                    match.position().lineIndex() + 1, // +1 for human-friendly 1-based indexing
                    match.position().startCharIndex() + 1
            );
            case "fromFileStart" -> String.format(
                    "Word \"%s\" was found at absolute position of %d",
                    match.word(),
                    match.position().fromFileStartCharIndex()
            );
            default -> String.format(
                    "[%s] Line: %d, IdxInCol: %d, Absolute: %d",
                    match.word(),
                    match.position().lineIndex() + 1,
                    match.position().startCharIndex() + 1,
                    match.position().fromFileStartCharIndex()
            );
        };
    }
}
