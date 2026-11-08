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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.lucene.analysis.hunspell.Dictionary;
import org.apache.lucene.analysis.hunspell.Hunspell;
import org.apache.lucene.analysis.hunspell.SortingStrategy;

/**
 * Validates words against a Hunspell dictionary and an optional whitelist file.
 * Falls back to an embedded English dictionary if no custom dictionary is configured.
 */
public final class SpellChecker {

  /** Path to the embedded default English affix resource. */
  private static final String DEFAULT_AFF_RESOURCE =
      "/io/github/praveen7294/checkstyle/spellcheck/dictionaries/en_US.aff";

  /** Path to the embedded default English dictionary resource. */
  private static final String DEFAULT_DIC_RESOURCE =
      "/io/github/praveen7294/checkstyle/spellcheck/dictionaries/en_US.dic";

  /** The underlying Hunspell dictionary and morphology engine. */
  private final Hunspell hunspell;

  /** Set of approved custom words and acronyms normalized to lowercase. */
  private final Set<String> whitelist;

  /** Internal thread-safe cache for validated words. */
  private final ConcurrentMap<String, Boolean> wordCache = new ConcurrentHashMap<>();

  /**
   * Constructs a SpellChecker using the embedded default English dictionary and no whitelist.
   *
   * @throws IOException if an error occurs reading dictionary resources
   * @throws ParseException if parsing the dictionary rules fails
   */
  public SpellChecker() throws IOException, ParseException {
    this(null, null, null);
  }

  /**
   * Constructs a SpellChecker using the embedded default English dictionary and a whitelist file.
   *
   * @param whitelistPath path to custom whitelist file (optional, can be null or empty)
   * @throws IOException if an error occurs reading dictionary or whitelist files
   * @throws ParseException if parsing the dictionary rules fails
   */
  public SpellChecker(String whitelistPath) throws IOException, ParseException {
    this(null, null, whitelistPath);
  }

  /**
   * Constructs a SpellChecker with optional custom dictionary, affix, and whitelist paths.
   * If {@code dictionaryPath} and {@code affixPath} are null or blank, the embedded default
   * English dictionary is used.
   *
   * @param dictionaryPath path to custom Hunspell .dic file (optional)
   * @param affixPath path to custom Hunspell .aff file (optional)
   * @param whitelistPath  path to custom whitelist file (optional)
   * @throws IllegalArgumentException if only one custom dictionary file is provided or file missing
   * @throws IOException if an error occurs reading any file
   * @throws ParseException if the Hunspell dictionary files are malformed
   */
  public SpellChecker(String dictionaryPath, String affixPath, String whitelistPath)
      throws IOException, ParseException {
    this.hunspell = loadHunspell(dictionaryPath, affixPath);
    this.whitelist = loadWhitelist(whitelistPath);
  }

  /**
   * Creates a Hunspell instance from the provided affix and dictionary input streams.
   *
   * @param affStream input stream for the Hunspell affix rules
   * @param dicStream input stream for the Hunspell dictionary words
   * @return initialized {@link Hunspell} instance
   * @throws IOException if an I/O error occurs
   * @throws ParseException if parsing the dictionary or affix rules fails
   */
  private static Hunspell createHunspell(InputStream affStream, InputStream dicStream)
      throws IOException, ParseException {
    final Dictionary dictionary = new Dictionary(
        affStream,
        List.of(dicStream),
        true,
        SortingStrategy.inMemory()
    );
    return new Hunspell(dictionary);
  }

  /**
   * Loads and initializes the Hunspell engine from external files or embedded resources.
   *
   * @param dicPath optional path to external .dic file
   * @param affPath optional path to external .aff file
   * @return initialized {@link Hunspell} instance
   * @throws IllegalArgumentException if only one dictionary file path is provided,
   *     or if a specified file does not exist or is a directory
   * @throws IOException if an error occurs reading the dictionary streams
   * @throws ParseException if parsing the dictionary or affix rules fails
   */
  private static Hunspell loadHunspell(String dicPath, String affPath)
      throws IOException, ParseException {
    final boolean hasDic = dicPath != null && !dicPath.isBlank();
    final boolean hasAff = affPath != null && !affPath.isBlank();

    if (hasDic != hasAff) {
      throw new IllegalArgumentException(
          "Both dictionaryPath and affixPath must be provided when using a custom dictionary");
    }

    if (hasDic) {
      final File affFile = new File(affPath);
      final File dicFile = new File(dicPath);

      if (!affFile.exists() || !affFile.isFile()) {
        throw new IllegalArgumentException("File not found: " + affPath);
      }
      if (!dicFile.exists() || !dicFile.isFile()) {
        throw new IllegalArgumentException("File not found: " + dicPath);
      }

      try (InputStream affStream = new BufferedInputStream(new FileInputStream(affFile));
           InputStream dicStream = new BufferedInputStream(new FileInputStream(dicFile))) {
        return createHunspell(affStream, dicStream);
      }
    }

    try (InputStream affStream = new BufferedInputStream(
        Objects.requireNonNull(
        SpellChecker.class.getResourceAsStream(DEFAULT_AFF_RESOURCE),
        "Default affix resource not found: " + DEFAULT_AFF_RESOURCE));
         InputStream dicStream = new BufferedInputStream(
             Objects.requireNonNull(
             SpellChecker.class.getResourceAsStream(DEFAULT_DIC_RESOURCE),
             "Default dictionary resource not found: " + DEFAULT_DIC_RESOURCE))) {
      return createHunspell(affStream, dicStream);
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
