package pokhval.wordsSearcher;

import pokhval.wordsSearcher.concurrency.ParallelSearchCoordinator;
import pokhval.wordsSearcher.searchAlgorithms.ACSearcher;
import pokhval.wordsSearcher.searchAlgorithms.KMPSearcher;
import pokhval.wordsSearcher.searchAlgorithms.RabinKarpSearcher;
import pokhval.wordsSearcher.searchAlgorithms.TextSearcher;
import pokhval.wordsSearcher.util.PositionTracker;
import pokhval.wordsSearcher.util.ResultPrinter;
import pokhval.wordsSearcher.util.WordMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Main {
    static String filePath = null;
    static String outputFilePath = null;
    static boolean caseSensitive = true;
    static String positionStyle = "default";
    static String algorithm = "AC";
    static List<String> keywords = new ArrayList<>();

    private static void checkIfFollowingValueProvided(String[] args, int index) {
        if (index + 1 >= args.length) {
            System.err.println("Error: Missing value for option " + args[index]);
            System.exit(1);
        }
    }

    private static void processArgs(String[] args) {
        int i = 0;
        while (i < args.length) {
            String token = args[i];

            switch (token) {
                case "-f" -> {
                    checkIfFollowingValueProvided(args, i);
                    filePath = args[i + 1];
                    i += 2;
                }
                case "-o" -> {
                    checkIfFollowingValueProvided(args, i);
                    outputFilePath = args[i + 1];
                    i += 2;
                }
                case "-cs" -> {
                    checkIfFollowingValueProvided(args, i);
                    caseSensitive = Boolean.parseBoolean(args[i + 1]);
                    i += 2;
                }
                case "-ps" -> {
                    checkIfFollowingValueProvided(args, i);
                    positionStyle = args[i + 1];
                    i += 2;
                }
                case "-a" -> {
                    checkIfFollowingValueProvided(args, i);
                    algorithm = args[i + 1];
                    i += 2;
                }
                default -> {
                    keywords.add(token);
                    i += 1;
                }
            }
        }

        if (filePath == null) {
            System.err.println("Are you missing filename?");
        }
        if (keywords.isEmpty()) {
            System.err.println("Are you missing keywords?");
        } else if  (keywords.size() > 1 && !algorithm.equals("AC")) {
            System.err.println("Only AC searches for multiple words, sadly");
        }
        if (!algorithm.equals("AC") && !algorithm.equals("KMP") && !algorithm.equals("RK")) {
            System.err.println("Please choose between AC and KMP or RK for algorithm");
        }
        if (!positionStyle.equals("default") && !positionStyle.equals("inLine") && !positionStyle.equals("fromFileStart")) {
            System.err.println("Please choose output style between default, inLine and fromFileStart");
        }

        if (filePath == null || keywords.isEmpty() ||
                (keywords.size() > 1 && !algorithm.equals("AC")) ||
                (!algorithm.equals("AC") && !algorithm.equals("KMP") && !algorithm.equals("RK")) ||
                (!positionStyle.equals("default") && !positionStyle.equals("inLine") && !positionStyle.equals("fromFileStart"))) {

            System.err.println("\nExiting due to invalid CLI parameters configuration.");
            System.exit(1);
        }
    }

    /**
     *
     * @param args:
     *  -f, filename after flag
     *  -o, output file path, in case user wants to write out into their file
     *  -cs, caseSensitive flag
     *  -ps, position message style
     *  -a, chosen algorithm to searchWords, AC is default
     *  - words can be randomly written in line when no flag is given
     */
    public static void main(String[] args) throws Exception {
        processArgs(args);

        int chunkSize = 5_000_000; // almost 10 Mb if char is 2B (as I use ASCII)
        TextSearcher textSearcher;

        Function<PositionTracker, TextSearcher> factory = switch (algorithm) {
            case "AC" -> (tracker) -> new ACSearcher(keywords, tracker);
            case "KMP" -> (tracker) -> new KMPSearcher(keywords.getFirst(), tracker);
            case "RK" -> (tracker) -> new RabinKarpSearcher(keywords.getFirst(), tracker);
            default -> throw new IllegalArgumentException("Unknown algorithm");
        };

        int maxWordLength = keywords.stream().mapToInt(String::length).max().orElse(1);

        ParallelSearchCoordinator coordinator = new ParallelSearchCoordinator(caseSensitive);
        List<WordMatch> results = coordinator.executeSearch(filePath, chunkSize, maxWordLength, factory);

        ResultPrinter.printToOutputStream(results, positionStyle, outputFilePath);
    }
}
