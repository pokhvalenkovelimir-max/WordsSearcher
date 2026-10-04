package pokhval.wordsSearcher.util;

public enum TokenType {
    /**
     * NewWord - means somewhere in the processed line a new word is discovered,
     *           mostly a non-white character appearing after the white once,
     *           this type of token provides a character
     * EndOfWord - means a whitespace appeared after a word, doesn't provide character
     * Whitespace - no data provided, exists only to account position of word in text
     * NewLine - means a newWord after EndOfLine, needed to account words position
     *           provides a character
     * EndOfLine - WhiteSpace meaning the end of line, no data provided
     */
    NewWord,
    InWord,
    EndOfWord,
    WhiteSpace,
    NewLine,
    EndOfFile
}
