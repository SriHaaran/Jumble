package asia.fourtitude.interviewq.jumble.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JumbleEngine {
    public static final String WORD_FILE_PATH = "words.txt";
    private static final Logger log = LoggerFactory.getLogger(JumbleEngine.class);
    private final Set<String> wordSet;

    public JumbleEngine() {
        this.wordSet = loadWordListFromFile();
    }

    private Set<String> loadWordListFromFile() {
        Set<String> words = new HashSet<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(WORD_FILE_PATH)) {
            if (inputStream == null) throw new FileNotFoundException("File not found: " + WORD_FILE_PATH);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(String::toLowerCase)
                    .forEach(words::add);

        } catch (IOException e) {
            log.error("Error reading the word file: {}", e.getMessage(), e);
            throw new UncheckedIOException("Error reading the word file at " + WORD_FILE_PATH + ": " + e.getMessage(), e);
        }
        return words;
    }


    /**
     * From the input `word`, produces/generates a copy which has the same
     * letters, but in different ordering.
     *
     * Example: from "elephant" to "aeehlnpt".
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#scramble()
     * b) scrambled letters/output must not be the same as input
     *
     * @param word  The input word to scramble the letters.
     * @return  The scrambled output/letters.
     */
    public String scramble(String word) {
        if (word == null || word.length() < 2) return word;

        List<Character> characters = word.chars().mapToObj(c -> (char) c).collect(Collectors.toList());

        String scrambled;
        do {
            Collections.shuffle(characters);
            scrambled = characters.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining());
        } while (scrambled.equals(word));

        return scrambled;
    }

    /**
     * Retrieves the palindrome words from the internal
     * word list/dictionary ("src/main/resources/words.txt").
     *
     * Word of single letter is not considered as valid palindrome word.
     *
     * Examples: "eye", "deed", "level".
     *
     * Evaluation/Grading:
     * a) able to access/use resource from classpath
     * b) using inbuilt Collections
     * c) using "try-with-resources" functionality/statement
     * d) pass unit test: JumbleEngineTest#palindrome()
     *
     * @return The list of palindrome words found in system/engine.
     * @see <a href="https://www.google.com/search?q=palindrome+meaning">Palindrome Meaning</a>
     */
    public Collection<String> retrievePalindromeWords() {
        return wordSet.stream()
                .filter(word -> word.length() > 1 && isPalindrome(word))
                .collect(Collectors.toList());
    }

    /**
     * Checks if a given word is a palindrome.
     * Ignores case sensitivity.
     *
     * @param word The word to check.
     * @return true if the word is a palindrome, false otherwise.
     */
    private boolean isPalindrome(String word) {
        String normalizedWord = word.toLowerCase();
        return normalizedWord.contentEquals(new StringBuilder(normalizedWord).reverse());
    }

    /**
     * Picks one word randomly from internal word list.
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#randomWord()
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param length  The word picked, must of length.
     *                When length is null, then return random word of any length.
     * @return  One of the word (randomly) from word list.
     *          Or null if none matching.
     */
    public String pickOneRandomWord(Integer length) {
        List<String> wordsToChooseFrom = length == null ? new ArrayList<>(wordSet) : filterWordsByLength(length);
        return wordsToChooseFrom.isEmpty() ? null : pickRandomWord(wordsToChooseFrom);
    }

    private List<String> filterWordsByLength(int length) {
        return wordSet.stream()
                .filter(word -> word.length() == length)
                .collect(Collectors.toList());
    }

    private String pickRandomWord(List<String> words) {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }


    /**
     * Checks if the `word` exists in internal word list.
     * Matching is case insensitive.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word  The input word to check.
     * @return  true if `word` exists in internal word list.
     */
    public boolean exists(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }

        return wordSet.contains(word.trim().toLowerCase());
    }

    /**
     * Finds all the words from internal word list which begins with the
     * input `prefix`.
     * Matching is case insensitive.
     *
     * Invalid `prefix` (null, empty string, blank string, non letter) will
     * return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param prefix  The prefix to match.
     * @return  The list of words matching the prefix.
     */
    public Collection<String> wordsMatchingPrefix(String prefix) {
        if (isInvalidPrefix(prefix)) {
            return Collections.emptyList();
        }

        return wordSet.stream()
                .filter(word -> word.startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }

    private boolean isInvalidPrefix(String prefix) {
        return prefix == null || prefix.trim().isEmpty() || !prefix.matches("[a-zA-Z]+");
    }

    /**
     * Finds all the words from internal word list that is matching
     * the searching criteria.
     *
     * `startChar` and `endChar` must be 'a' to 'z' only. And case insensitive.
     * `length`, if have value, must be positive integer (>= 1).
     *
     * Words are filtered using `startChar` and `endChar` first.
     * Then apply `length` on the result, to produce the final output.
     *
     * Must have at least one valid value out of 3 inputs
     * (`startChar`, `endChar`, `length`) to proceed with searching.
     * Otherwise, return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param startChar  The first character of the word to search for.
     * @param endChar    The last character of the word to match with.
     * @param length     The length of the word to match.
     * @return  The list of words matching the searching criteria.
     */
    public Collection<String> searchWords(Character startChar, Character endChar, Integer length) {
        if (areAllFiltersInvalid(startChar, endChar, length)) {
            return Collections.emptyList();
        }

        return wordSet.stream()
                .filter(word -> matchesStartChar(word, startChar))
                .filter(word -> matchesEndChar(word, endChar))
                .filter(word -> matchesLength(word, length))
                .collect(Collectors.toList());
    }

    private boolean areAllFiltersInvalid(Character startChar, Character endChar, Integer length) {
        return isInvalidChar(startChar) && isInvalidChar(endChar) && isInvalidLength(length);
    }

    private boolean isInvalidChar(Character c) {
        return c == null || !Character.isLetter(c);
    }

    private boolean isInvalidLength(Integer length) {
        return length == null || length < 1;
    }

    private boolean matchesStartChar(String word, Character startChar) {
        return isInvalidChar(startChar) || word.startsWith(String.valueOf(startChar).toLowerCase());
    }

    private boolean matchesEndChar(String word, Character endChar) {
        return isInvalidChar(endChar) || word.endsWith(String.valueOf(endChar).toLowerCase());
    }

    private boolean matchesLength(String word, Integer length) {
        return isInvalidLength(length) || word.length() == length;
    }

    /**
     * Generates all possible combinations of smaller/sub words using the
     * letters from input word.
     *
     * The `minLength` set the minimum length of sub word that is considered
     * as acceptable word.
     *
     * If length of input `word` is less than `minLength`, then return empty list.
     *
     * The sub words must exist in internal word list.
     *
     * Example: From "yellow" and `minLength` = 3, the output sub words:
     *     low, lowly, lye, ole, owe, owl, well, welly, woe, yell, yeow, yew, yowl
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word       The input word to use as base/seed.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   Expects positive integer.
     *                   Default is 3.
     * @return  The list of sub words constructed from input `word`.
     */
    public Collection<String> generateSubWords(String word, Integer minLength) {
        final int DEFAULT_MIN_LENGTH = 3;
        int minLen = minLength == null ? DEFAULT_MIN_LENGTH : minLength;

        if (!isValidWord(word) || minLen < 1 || word.length() < minLen) {
            return Collections.emptyList();
        }

        word = word.toLowerCase();
        Set<String> result = new HashSet<>();
        char[] letters = word.toCharArray();
        Arrays.sort(letters); // Sorting helps handle duplicates better

        // Generate all permutations from length = minLen to word.length()
        for (int len = minLen; len <= letters.length; len++) {
            generatePermutations(letters, new boolean[letters.length], new StringBuilder(), len, result);
        }

        result.remove(word); // remove the original word
        return result.stream()
                .filter(wordSet::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Helper method to generate all unique permutations of a given length.
     */
    private void generatePermutations(char[] letters, boolean[] used, StringBuilder current, int targetLength, Set<String> result) {
        if (current.length() == targetLength) {
            result.add(current.toString());
            return;
        }

        for (int i = 0; i < letters.length; i++) {
            if (used[i]) continue;

            // Skip duplicates: if current char == previous and previous not used
            if (i > 0 && letters[i] == letters[i - 1] && !used[i - 1]) continue;

            used[i] = true;
            current.append(letters[i]);
            generatePermutations(letters, used, current, targetLength, result);
            current.deleteCharAt(current.length() - 1);
            used[i] = false;
        }
    }

    private boolean isValidWord(String word) {
        return word != null && !word.trim().isEmpty() && word.matches("[a-zA-Z]+");
    }

    /**
     * Creates a game state with word to guess, scrambled letters, and
     * possible combinations of words.
     *
     * Word is of length 6 characters.
     * The minimum length of sub words is of length 3 characters.
     *
     * @param length     The length of selected word.
     *                   Expects >= 3.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   Expects positive integer.
     *                   Default is 3.
     * @return  The game state.
     */
    public GameState createGameState(Integer length, Integer minLength) {
        Objects.requireNonNull(length, "length must not be null");
        if (minLength == null) {
            minLength = 3;
        } else if (minLength <= 0) {
            throw new IllegalArgumentException("Invalid minLength=[" + minLength + "], expect positive integer");
        }
        if (length < 3) {
            throw new IllegalArgumentException("Invalid length=[" + length + "], expect greater than or equals 3");
        }
        if (minLength > length) {
            throw new IllegalArgumentException("Expect minLength=[" + minLength + "] greater than length=[" + length + "]");
        }
        String original = this.pickOneRandomWord(length);
        if (original == null) {
            throw new IllegalArgumentException("Cannot find valid word to create game state");
        }
        String scramble = this.scramble(original);
        Map<String, Boolean> subWords = new TreeMap<>();
        for (String subWord : this.generateSubWords(original, minLength)) {
            subWords.put(subWord, Boolean.FALSE);
        }
        return new GameState(original, scramble, subWords);
    }

}
