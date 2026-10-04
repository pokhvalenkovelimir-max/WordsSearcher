package pokhval.wordsSearcher.concurrency;

import pokhval.wordsSearcher.searchAlgorithms.TextSearcher;
import pokhval.wordsSearcher.util.PositionTracker;
import pokhval.wordsSearcher.util.WordMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class ParallelSearchCoordinator {
    boolean isCaseSensitive;

    public ParallelSearchCoordinator(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public List<WordMatch> executeSearch(
            String filePath,
            int chunkSize,
            int maxWordLength,
            Function<PositionTracker, TextSearcher> searcherFactory
    ) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();

        try (ExecutorService executor = Executors.newFixedThreadPool(cores)) {
            List<Future<List<WordMatch>>> futures = new ArrayList<>();

            TextDivider.divideAndProcess(
                    filePath,
                    chunkSize,
                    maxWordLength,
                    (chunkText, startingTracker) -> {
                        TextSearcher individualSearcher = searcherFactory.apply(startingTracker);
                        SearchWorker worker = new SearchWorker(chunkText, individualSearcher, isCaseSensitive);
                        futures.add(executor.submit(worker));
            });

            // results are collected, but duplicates shall be deleted and results must be sorted
            List<WordMatch> allMatches = new ArrayList<>();
            for (Future<List<WordMatch>> future : futures) {
                allMatches.addAll(future.get()); // Waits for threads to finish
            }

            executor.shutdown();
            return allMatches;
        } catch (Exception e)  {
            e.printStackTrace();
            return null;
        }
    }
}
