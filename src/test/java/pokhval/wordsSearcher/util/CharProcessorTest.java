package pokhval.wordsSearcher.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharProcessorTest {

    @ParameterizedTest
    @ValueSource(chars = {' ', '\t', '\r'})
    void testLineStartToWhitespaceTransitions(char whitespaceChar) {
        CharProcessor lexer = new CharProcessor(true);

        Token token = lexer.processChar(whitespaceChar);

        assertEquals(TokenType.WhiteSpace, token.type());
    }

    @Test
    void testStandardWordTransition() {
        CharProcessor lexer = new CharProcessor(true);

        Token token = lexer.processChar('M');

        assertEquals(TokenType.NewWord, token.type());
        assertEquals('M', token.value());
    }

    @Test
    void testSampleText() {
        CharProcessor lexer = new CharProcessor(true);
        ArrayList<Token> actualTokens = new ArrayList<>();

        String sampleText = "This is a sample. \n \n Great\n";
        for (char c : sampleText.toCharArray()) {
            actualTokens.add(lexer.processChar(c));
        }

        List<Token> expectedTokens = List.of(
                new Token(TokenType.NewWord, 'T'),
                new Token(TokenType.InWord, 'h'),
                new Token(TokenType.InWord, 'i'),
                new Token(TokenType.InWord, 's'),
                new Token(TokenType.EndOfWord),

                new Token(TokenType.NewWord, 'i'),
                new Token(TokenType.InWord, 's'),
                new Token(TokenType.EndOfWord),

                new Token(TokenType.NewWord, 'a'),
                new Token(TokenType.EndOfWord),

                new Token(TokenType.NewWord, 's'),
                new Token(TokenType.InWord, 'a'),
                new Token(TokenType.InWord, 'm'),
                new Token(TokenType.InWord, 'p'),
                new Token(TokenType.InWord, 'l'),
                new Token(TokenType.InWord, 'e'),
                new Token(TokenType.InWord, '.'),
                new Token(TokenType.EndOfWord),

                new Token(TokenType.NewLine),
                new Token(TokenType.WhiteSpace),
                new Token(TokenType.NewLine),
                new Token(TokenType.WhiteSpace),

                new Token(TokenType.NewWord, 'G'),
                new Token(TokenType.InWord, 'r'),
                new Token(TokenType.InWord, 'e'),
                new Token(TokenType.InWord, 'a'),
                new Token(TokenType.InWord, 't'),
                new Token(TokenType.NewLine)
        );

        assertEquals(expectedTokens, actualTokens);
    }
}