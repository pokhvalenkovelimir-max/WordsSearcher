package pokhval.wordsSearcher.util;

public class CharProcessor {
    LexerState currentState = LexerState.LINE_START;
    boolean caseSensitive = false;

    private enum LexerState {
        LINE_START,
        WHITESPACE,
        WORD
    }

    public CharProcessor(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public Token processChar(char c) {
        boolean isWhiteSpace = Character.isWhitespace(c);
        boolean isNewLine = (c == '\n');

        if (!caseSensitive) {
            c =  Character.toLowerCase(c);
        }

        switch(currentState) {
            case WORD ->  {
                if (isNewLine) {
                    currentState = LexerState.LINE_START;
                    return new Token(TokenType.NewLine, c);
                } else if (isWhiteSpace) {    // includes /r automatically
                    currentState = LexerState.WHITESPACE;
                    return new Token(TokenType.EndOfWord, c);
                } else {
                    return new Token(TokenType.InWord, c);
                }
            }
            case WHITESPACE ->  {
                if (isNewLine) {
                    this.currentState = LexerState.LINE_START;
                    return new Token(TokenType.NewLine, c);
                } else if (isWhiteSpace) {
                    return new Token(TokenType.WhiteSpace, c);
                } else {
                    this.currentState = LexerState.WORD;
                    return new Token(TokenType.NewWord, c);
                }
            }
            case LINE_START ->  {
                if (isNewLine) {
                    return new Token(TokenType.NewLine, c);
                } if (isWhiteSpace) {
                    this.currentState = LexerState.WHITESPACE;
                    return new Token(TokenType.WhiteSpace, c);
                } else {
                    this.currentState = LexerState.WORD;
                    return new Token(TokenType.NewWord, c);
                }
            }
            default ->  {
                throw new IllegalStateException("Unexpected token: " + currentState);
            }
        }
    }
}
