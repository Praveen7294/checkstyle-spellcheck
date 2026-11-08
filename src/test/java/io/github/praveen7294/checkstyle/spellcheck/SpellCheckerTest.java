package io.github.praveen7294.checkstyle.spellcheck;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SpellChecker} verifying 100% instruction and branch coverage.
 */
public class SpellCheckerTest {

  private static final String DIC_PATH = "src/test/resources/dictionaries/test.dic";
  private static final String AFF_PATH = "src/test/resources/dictionaries/test.aff";
  private static final String WHITELIST_PATH = "src/test/resources/dictionaries/whitelist.txt";
  private static final String DIR_PATH = "src/test/resources/dictionaries";

  private SpellChecker spellChecker;

  @BeforeEach
  public void setUp() throws Exception {
    spellChecker = new SpellChecker(DIC_PATH, AFF_PATH, WHITELIST_PATH);
  }

  @Test
  public void testDefaultConstructor() throws Exception {
    final SpellChecker checker = new SpellChecker();
    assertTrue(checker.isCorrect("user"));
    assertFalse(checker.isCorrect("adress"));
  }

  @Test
  public void testSingleArgWhitelistConstructor() throws Exception {
    final SpellChecker checker = new SpellChecker(WHITELIST_PATH);
    assertTrue(checker.isCorrect("user"));
    assertTrue(checker.isCorrect("dto"));
    assertFalse(checker.isCorrect("adress"));
  }

  @Test
  public void testBothDictionaryAndAffixPathsBlankFallsBackToDefault() throws Exception {
    final SpellChecker checker = new SpellChecker("   ", "   ", null);
    assertTrue(checker.isCorrect("user"));
    assertFalse(checker.isCorrect("adress"));
  }

  @Test
  public void testValidDictionaryWords() {
    assertTrue(spellChecker.isCorrect("user"));
    assertTrue(spellChecker.isCorrect("name"));
    assertTrue(spellChecker.isCorrect("line"));
    assertTrue(spellChecker.isCorrect("number"));
    assertTrue(spellChecker.isCorrect("get"));
  }

  @Test
  public void testCaseInsensitivity() {
    assertTrue(spellChecker.isCorrect("User"));
    assertTrue(spellChecker.isCorrect("NAME"));
    assertTrue(spellChecker.isCorrect("GeT"));
  }

  @Test
  public void testWhitelistWords() {
    assertTrue(spellChecker.isCorrect("dto"));
    assertTrue(spellChecker.isCorrect("DTO"));
    assertTrue(spellChecker.isCorrect("dao"));
    assertTrue(spellChecker.isCorrect("uuid"));
    assertTrue(spellChecker.isCorrect("  DTO  "));
  }

  @Test
  public void testMisspelledWords() {
    assertFalse(spellChecker.isCorrect("adress"));
    assertFalse(spellChecker.isCorrect("calender"));
    assertFalse(spellChecker.isCorrect("usr"));
  }

  @Test
  public void testNullAndBlankWords() {
    assertTrue(spellChecker.isCorrect(null));
    assertTrue(spellChecker.isCorrect(""));
    assertTrue(spellChecker.isCorrect("   "));
  }

  @Test
  public void testOptionalWhitelistOmittedOrBlank() throws Exception {
    // Tests whitelistPath == null
    final SpellChecker checkerNullWhitelist = new SpellChecker(DIC_PATH, AFF_PATH, null);
    assertTrue(checkerNullWhitelist.isCorrect("user"));
    assertFalse(checkerNullWhitelist.isCorrect("dto"));

    // Tests whitelistPath.isBlank() with empty string
    final SpellChecker checkerEmptyWhitelist = new SpellChecker(DIC_PATH, AFF_PATH, "");
    assertTrue(checkerEmptyWhitelist.isCorrect("user"));
    assertFalse(checkerEmptyWhitelist.isCorrect("dto"));

    // Tests whitelistPath.isBlank() with whitespace string
    final SpellChecker checkerWhitespaceWhitelist = new SpellChecker(DIC_PATH, AFF_PATH, "   ");
    assertTrue(checkerWhitespaceWhitelist.isCorrect("user"));
    assertFalse(checkerWhitespaceWhitelist.isCorrect("dto"));
  }

  @Test
  public void testDictionaryPathNullOrBlank() {
    // Tests dictionaryPath == null
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker(null, AFF_PATH, null));

    // Tests dictionaryPath.isBlank()
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker("", AFF_PATH, null));
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker("   ", AFF_PATH, null));
  }

  @Test
  public void testAffixPathNullOrBlank() {
    // Tests affixPath == null
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker(DIC_PATH, null, null));

    // Tests affixPath.isBlank()
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker(DIC_PATH, "", null));
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker(DIC_PATH, "   ", null));
  }

  @Test
  public void testFileDoesNotExistValidation() {
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker(DIC_PATH, "non_existent.aff", null));
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker("non_existent.dic", AFF_PATH, null));
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker(DIC_PATH, AFF_PATH, "non_existent_whitelist.txt"));
  }

  @Test
  public void testDirectoryProvidedInsteadOfFileValidation() {
    // Tests !affFile.isFile()
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker(DIC_PATH, DIR_PATH, null));

    // Tests !dicFile.isFile()
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker(DIR_PATH, AFF_PATH, null));

    // Tests !Files.isRegularFile(path)
    assertThrows(IllegalArgumentException.class, () ->
      new SpellChecker(DIC_PATH, AFF_PATH, DIR_PATH));
  }

  @Test
  public void testWordCacheHits() {
    // 1. Dictionary word: 1st call is a cache miss, 2nd call is a cache hit
    assertTrue(spellChecker.isCorrect("user"));
    assertTrue(spellChecker.isCorrect("user"));

    // 2. Whitelist word: 1st call is a cache miss, 2nd call is a cache hit
    assertTrue(spellChecker.isCorrect("dto"));
    assertTrue(spellChecker.isCorrect("dto"));

    // 3. Misspelled word: 1st call caches false, 2nd call hits cache with false
    assertFalse(spellChecker.isCorrect("adress"));
    assertFalse(spellChecker.isCorrect("adress"));
  }
}
