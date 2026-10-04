# WordsSearcher Documentation

WordsSearcher is a high-performance concurrent command-line application used to search for word matches within text files.

## Prerequisites
* Java 21
* Maven 3.9+

## Essential Commands

Before running the application, compile the source files:

mvn compile

To execute the internal unit tests and verify code correctness: 

mvn test

Running the Application
The application is executed using mvn exec:java -Dexec.args="...". Configuration parameters are specified using the following CLI flags:

-f (Required): Path to the input text file. This path must be specified relative to the project root folder (WordsSearcher/).

-o (Optional, default: null): Path to the output file. If not provided, results are printed directly to the terminal standard output.

-cs (Optional, default: true): Toggles case-sensitivity during the pattern matching process (true/false).

-a (Optional, default: AC): Specifies the pattern matching algorithm to use. Options include:

	AC: Aho-Corasick

	KMP: Knuth-Morris-Pratt

	RK: Rabin-Karp

-ps (Optional, default: default): Specifies the output formatting style for match positions. Options include:

	default: Prints out all available match metadata.

	inLine: Prints the zero-based character index relative to the beginning of that specific line, along with the sequential line number itself.

	fromFileStart: Prints the absolute zero-based character index relative to the very first character of the entire file (counting newlines and spaces sequentially).

Search Terms: Any standalone words provided without a preceding flag are automatically treated as target keywords to search for.

Note: The order of flags and keywords is arbitrary. If an invalid flag or value is provided, an error message will be printed and execution will terminate immidiately.

Execution Examples

Example 1: Case-sensitive Aho-Corasick line search

mvn exec:java -Dexec.args="-f src/book.txt -a AC -ps inLine Hamlet \"To be, or not to be\""

Example 2: Rabin-Karp search saving results to a file

mvn exec:java -Dexec.args="-f src/book.txt -o result.txt -a RK Gutenberg"
