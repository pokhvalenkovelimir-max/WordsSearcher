package pokhval.wordsSearcher.searchAlgorithms;

import pokhval.wordsSearcher.util.CharProcessor;
import pokhval.wordsSearcher.util.WordMatch;

import java.util.List;

public interface TextSearcher {
    List<WordMatch> search(String textPart, CharProcessor cp);
}
