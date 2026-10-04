package pokhval.wordsSearcher.searchAlgorithms;

import pokhval.wordsSearcher.util.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class ACSearcher implements TextSearcher {
    List<String> searchedWords;
    ArrayList<Integer> automat;
    private int lineIndex = 0;  // not -1 because file doesnt start with \n
    private int startCharIndex = -1;
    private int fromFileStartCharIndex = -1;

    Node root;

    private static class Node {
        public List<Node> children = new ArrayList<>();
        public final char value;
        public boolean isSearched;
        public Node Back;
        public Node Shortcut;
        public String word;

        Node(char value) {
            this.value = value;
        }

        public Node canTransferWithCharTo(Character ch) {
            for (Node child : this.children) {
                if (child.value == ch) {
                    return child;
                }
            }

            return null;
        }
    }

    public ACSearcher(List<String> searchedWords, PositionTracker pt) {
        this.searchedWords = searchedWords;
        this.root = new Node('\0');

        this.lineIndex = pt.lineIndex();
        this.startCharIndex = pt.startCharIndex();
        this.fromFileStartCharIndex = pt.fromFileStartCharIndex();
        ACConstruction();
    }

    /**
     * Function inserts a new node with given char value if it doesn't exist at a given branch
     * @param c new child's character state, that has to be included
     * @param node current node in building process
     * @return new node to refer to, when continuing on building a branch
     */
    private Node insertCharIntoAutomat(char c, Node node) {
        for (Node child : node.children) {
            if (child.value == c) {
                // word will have its transitions through a state, that already exists
                return child;
            }
        }

        Node newNode = new Node(c);
        node.children.add(newNode);
        return newNode;
    }

    private void ACConstruction() {
        // part where all branches representing possible ways to build a word
        // from empty string are featured
        for (String word : searchedWords) {
            Node currentNode = this.root;
            for (char c : word.toCharArray()) {
                currentNode = insertCharIntoAutomat(c, currentNode);
            }
            currentNode.isSearched = true;
            currentNode.word = word;
            // only final words will be having word value
        }

        this.root.Back = this.root;
        this.root.Shortcut = null;
        this.root.isSearched = false;

        ArrayDeque<Node> nextTreeNodesToDefine = new ArrayDeque<>(this.root.children);

        for (Node child : nextTreeNodesToDefine) {
            child.Back = this.root;
            child.Shortcut = null;
        }

        while (!nextTreeNodesToDefine.isEmpty()) {
            Node current =  nextTreeNodesToDefine.poll();
            List<Node> currentChildren = current.children;

            for (Node child : currentChildren) {
                Node back = step(current.Back, child.value);
                child.Back = back;

                if (back.isSearched) {
                    child.Shortcut = back;
                } else {
                    child.Shortcut = back.Shortcut;
                }

                nextTreeNodesToDefine.addLast(child);
            }
        }
    }

    private Node step(Node state, char ch) {
        while (state.canTransferWithCharTo(ch) == null && state != root) {
            state = state.Back;
        }
        if (state.canTransferWithCharTo(ch) != null) {
            state =  state.canTransferWithCharTo(ch);
        }

        return state;
    }

    private Node processToken(Token t, Node currentState, List<WordMatch> matches) {

        currentState = step(currentState, t.value());

        Node nodeToCheckAllMatches = currentState;

        while (nodeToCheckAllMatches != null) {
            if (nodeToCheckAllMatches.isSearched) {
                String word = nodeToCheckAllMatches.word;
                matches.add(new WordMatch(
                        word,
                        new PositionTracker(
                                this.lineIndex,
                                this.startCharIndex - word.length() + 1,
                                this.fromFileStartCharIndex - word.length() + 1
                        )
                ));
            }

            nodeToCheckAllMatches = nodeToCheckAllMatches.Shortcut;
        }

        return currentState;
    }

    @Override
    public List<WordMatch> search(String textPart, CharProcessor cp) {
        List<WordMatch> wordMatches = new ArrayList<>();
        Node state = root;

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
