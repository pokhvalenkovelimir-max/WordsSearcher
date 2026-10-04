package pokhval.wordsSearcher.util;

/**
 * A simple holder
 * @param type TokenType value, which is decided by CharProcessor
 * @param value If TokenType Implies, a value will be provided,
 *              otherwise it holds a default value as in constructor below
 */
public record Token(TokenType type, char value) {

    /**
     * Overloaded constructor for tokens that does not require a character value.
     * @param type TokenType value
     */
    public Token(TokenType type) {
        this(type, '\0');
    }
}