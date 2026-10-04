package pokhval.wordsSearcher.searchAlgorithms;

import pokhval.wordsSearcher.util.*;

import java.util.ArrayList;
import java.util.List;

public class RabinKarpSearcher implements TextSearcher {
    String searchedWord;
    int hashMultiplier = 101; // 13 was chosen, because it's prime and not too big
    long moduloNumber = 2147483647L; // 2^31 - 1, apparently ^ is XOR in java
    long windowHash = 0;
    long searchedWordHash = 0;
    long upperCharWeight;
    int searchedWordLength;
    boolean caseSensitive = true;

    private final char[] circularWindowBuffer;
    private int bufferIndex = 0;

    private int lineIndex = 0;
    private int startCharIndex = -1;
    private int fromFileStartCharIndex = -1;

    public RabinKarpSearcher(String SearchedWord, PositionTracker pt) {
        this.searchedWord = SearchedWord;
        this.searchedWordLength = searchedWord.length();
        this.searchedWordHash = getSearchedWordHash(searchedWord);
        long upperCharWeight = 1;
        this.lineIndex = pt.lineIndex();
        this.startCharIndex = pt.startCharIndex();
        this.fromFileStartCharIndex = pt.fromFileStartCharIndex();

        for (int i = 0; i < searchedWord.length() - 1; i++) {
            upperCharWeight = (upperCharWeight * hashMultiplier) % moduloNumber;
        }

        this.upperCharWeight = upperCharWeight;
        this.circularWindowBuffer = new char[SearchedWord.length()];
    }

    boolean searchedWordInBuffer() {
        int index = bufferIndex;

        for (char c : this.searchedWord.toCharArray()) {
            if (c != circularWindowBuffer[index]) {
                return false;
            }
            index = (index + 1) % circularWindowBuffer.length;
        }

        return true;
    }

    private void processToken(Token t, List<WordMatch> matches) {
        char oldChar = circularWindowBuffer[bufferIndex];

        step(oldChar, t.value());
        circularWindowBuffer[bufferIndex] = t.value();

        this.bufferIndex = (this.bufferIndex + 1) % circularWindowBuffer.length;

        if (this.windowHash == this.searchedWordHash && searchedWordInBuffer()) {
            matches.add(new WordMatch(
                    this.searchedWord,
                    new PositionTracker(
                            this.lineIndex,
                            this.startCharIndex - this.searchedWordLength + 1,
                            this.fromFileStartCharIndex - this.searchedWordLength + 1
                    )
            ));
        }
    }

    long getSearchedWordHash(String searchedWord) {
        long searchedHash = 0;
        for (int i = 0; i < this.searchedWordLength; i++) {
            searchedHash = (searchedWord.charAt(i) + searchedHash * hashMultiplier) % moduloNumber;
        }

        return searchedHash;
    }

    void step(char oldChar, char newChar) {
        long removedValue = (oldChar * this.upperCharWeight) % moduloNumber;

        this.windowHash = (this.windowHash - removedValue + moduloNumber) % moduloNumber;

        this.windowHash = (this.windowHash * this.hashMultiplier + newChar) % moduloNumber;
    }

    @Override
    public List<WordMatch> search(String textPart, CharProcessor cp) {
        List<WordMatch> wordMatches = new ArrayList<>();

        for (Character ch : textPart.toCharArray()) {
            Token t = cp.processChar(ch);
            this.fromFileStartCharIndex += 1;

            switch (t.type()) {
                case TokenType.NewLine -> {
                    this.startCharIndex = -1;
                    this.lineIndex += 1;
                    processToken(t, wordMatches);
                }
                case TokenType.NewWord,
                     TokenType.InWord,
                     TokenType.EndOfWord,
                     TokenType.WhiteSpace -> {
                    this.startCharIndex += 1;
                    processToken(t, wordMatches);
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
