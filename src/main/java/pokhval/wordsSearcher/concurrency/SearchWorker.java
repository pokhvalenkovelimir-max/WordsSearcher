package pokhval.wordsSearcher.concurrency;

import pokhval.wordsSearcher.searchAlgorithms.TextSearcher;
import pokhval.wordsSearcher.util.CharProcessor;
import pokhval.wordsSearcher.util.WordMatch;

import java.util.List;
import java.util.concurrent.Callable;

public class SearchWorker implements Callable<List<WordMatch>> {
    private final String chunkText;
    private final TextSearcher searcher;
    private final CharProcessor charProcessor;

    SearchWorker(String chunkText, TextSearcher searcher, boolean isCaseSensitive) {
        this.chunkText = chunkText;
        this.searcher = searcher;
        this.charProcessor = new CharProcessor(isCaseSensitive);
    }

    @Override
    public List<WordMatch> call() throws Exception {
        return searcher.search(chunkText, charProcessor);
    }
}
