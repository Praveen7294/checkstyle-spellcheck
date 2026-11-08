package io.github.praveen7294.checkstyle.spellcheck;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.lucene.analysis.hunspell.Dictionary;
import org.apache.lucene.analysis.hunspell.Hunspell;
import org.apache.lucene.analysis.hunspell.SortingStrategy;

/**
 * Validates words against a user-configured Hunspell dictionary and an optional whitelist file.
 */
public final class SpellChecker {

  /** The underlying Hunspell dictionary and morphology engine. */
  private final Hunspell hunspell;

  /** Set of approved custom words and acronyms normalized to lowercase. */
  private final Set<String> whitelist;

  /**
   * Internal thread-safe cache for validated words.
   */
  private final ConcurrentMap<String, Boolean> wordCache = new ConcurrentHashMap<>();

  /**
   * Constructs a SpellChecker with the given dictionary, affix, and optional whitelist paths.
   *
   * @param dictionaryPath path to the Hunspell .dic file (mandatory)
   * @param affixPath      path to the Hunspell .aff file (mandatory)
   * @param whitelistPath  path to custom whitelist file (optional, can be null or empty)
   * @throws IllegalArgumentException if mandatory files are missing or do not exist
   * @throws IOException if an error occurs reading any file
   * @throws ParseException if the Hunspell dictionary files are malformed
   */
  public SpellChecker(String dictionaryPath, String affixPath, String whitelistPath)
      throws IOException, ParseException {
    if (dictionaryPath == null || dictionaryPath.isBlank()) {
      throw new IllegalArgumentException("dictionaryPath must not be null or blank");
    }
    if (affixPath == null || affixPath.isBlank()) {
      throw new IllegalArgumentException("affixPath must not be null or blank");
    }

    this.hunspell = loadHunspell(dictionaryPath, affixPath);
    this.whitelist = loadWhitelist(whitelistPath);
  }

  /**
   * Loads and initializes the Hunspell engine from the given dictionary and affix files.
   *
   * @param dicPath path to the Hunspell .dic file
   * @param affPath path to the Hunspell .aff file
   * @return initialized {@link Hunspell} instance
   * @throws IllegalArgumentException if either file does not exist or is not a regular file
   * @throws IOException if an error occurs while reading the files
   * @throws ParseException if parsing the dictionary or affix rules fails
   */
  private static Hunspell loadHunspell(String dicPath, String affPath)
      throws IOException, ParseException {
    final File affFile = new File(affPath);
    final File dicFile = new File(dicPath);

    if (!affFile.exists() || !affFile.isFile()) {
      throw new IllegalArgumentException("Affix file not found: " + affPath);
    }
    if (!dicFile.exists() || !dicFile.isFile()) {
      throw new IllegalArgumentException("Dictionary file not found: " + dicPath);
    }

    try (InputStream affStream = new BufferedInputStream(new FileInputStream(affFile));
         InputStream dicStream = new BufferedInputStream(new FileInputStream(dicFile))) {

      final Dictionary dictionary = new Dictionary(
          affStream,
          List.of(dicStream),
          true, // ignoreCase
          SortingStrategy.inMemory()
      );
      return new Hunspell(dictionary);
    }
  }

  /**
   * Loads and normalizes custom whitelist words from the specified file.
   *
   * @param whitelistPath path to the whitelist file (can be null or blank)
   * @return immutable set of normalized lowercase words, or an empty set if no path is specified
   * @throws IllegalArgumentException if the specified whitelist file does not exist
   * @throws IOException if an error occurs while reading the file
   */
  private static Set<String> loadWhitelist(String whitelistPath) throws IOException {
    if (whitelistPath == null || whitelistPath.isBlank()) {
      return Collections.emptySet();
    }

    final Path path = Path.of(whitelistPath);
    if (!Files.exists(path) || !Files.isRegularFile(path)) {
      throw new IllegalArgumentException("Whitelist file not found: " + whitelistPath);
    }

    final List<String> lines = Files.readAllLines(path);
    final Set<String> words = new HashSet<>();
    for (String line : lines) {
      final String trimmed = line.trim();
      // Ignore empty lines and comments
      if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !trimmed.startsWith("//")) {
        words.add(trimmed.toLowerCase(Locale.ROOT));
      }
    }
    return Collections.unmodifiableSet(words);
  }

  /**
   * Checks if a word is spelled correctly according to the whitelist or Hunspell dictionary.
   *
   * @param word the word to validate
   * @return true if valid, false otherwise
   */
  public boolean isCorrect(String word) {
    if (word == null || word.isBlank()) {
      return true;
    }
    final String normalized = word.trim().toLowerCase(Locale.ROOT);
    final Boolean cached = wordCache.get(normalized);
    if (cached != null) {
      return cached;
    }
    final boolean valid = whitelist.contains(normalized) || hunspell.spell(normalized);
    wordCache.put(normalized, valid);
    return valid;
  }
}
