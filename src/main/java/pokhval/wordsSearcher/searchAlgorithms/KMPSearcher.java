package pokhval.wordsSearcher.searchAlgorithms;

import pokhval.wordsSearcher.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KMPSearcher implements TextSearcher {
    String searchedWord;
    ArrayList<Integer> automat;
    private int lineIndex = 0;  // not -1 because file doesnt start with \n
    private int startCharIndex = -1;
    private int fromFileStartCharIndex = -1;

    public KMPSearcher(String word, PositionTracker pt) {
        this.searchedWord = word;
        this.automat = new ArrayList<>(Collections.nCopies(word.length() + 1, 0));
        this.lineIndex = pt.lineIndex();
        this.startCharIndex = pt.startCharIndex();
        this.fromFileStartCharIndex = pt.fromFileStartCharIndex();
        constructDFA();
    }

    private void constructDFA() {
        int innerState = 0;
        automat.set(0, -1);
        automat.set(1, 0);

        for (int j = 2; j <= searchedWord.length(); j++) {
            innerState = step(innerState, this.searchedWord.charAt(j-1));
            automat.set(j, innerState);
        }
    }

    private int step(int state, char ch) {
        while (state == searchedWord.length() || (searchedWord.charAt(state) != ch && state != 0)) {
            state = automat.get(state);
        }
        if (searchedWord.charAt(state) == ch) {
            state++;
        }

        return state;
    }

    private int processToken(Token t, int currentState, List<WordMatch> matches) {

        currentState = step(currentState, t.value());

        if (currentState == searchedWord.length()) {
            matches.add(new WordMatch(
                    this.searchedWord,
                    new PositionTracker(
                            this.lineIndex,
                            this.startCharIndex - this.searchedWord.length() + 1,
                            this.fromFileStartCharIndex - this.searchedWord.length() + 1
                    )
            ));
        }

        return currentState;
    }

    @Override
    public List<WordMatch> search(String textPart, CharProcessor cp) {
        List<WordMatch> wordMatches = new ArrayList<>();
        int state = 0;

        for (Character ch : textPart.toCharArray()) {
            Token t = cp.processChar(ch);
            this.fromFileStartCharIndex += 1;   // if I count \n as a char in text
            // I've decided I will not ignore whitespaces when searching for user-prompted words matches
            switch (t.type()) {
                case TokenType.NewLine -> {
                    this.startCharIndex = -1;
                    this.lineIndex += 1;
                    state = processToken(t, state, wordMatches);
                }
                case TokenType.NewWord,
                     TokenType.InWord,
                     TokenType.EndOfWord,
                     TokenType.WhiteSpace -> {
                    this.startCharIndex += 1;
                    state = processToken(t, state, wordMatches);
                }
                case TokenType.EndOfFile -> {
                }  //break is there automatically
                default -> {
                    throw new RuntimeException("Unexpected token type: " + t.type());
                }
            }
        }

        return wordMatches;
    }
}
