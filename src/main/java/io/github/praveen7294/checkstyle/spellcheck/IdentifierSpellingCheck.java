package io.github.praveen7294.checkstyle.spellcheck;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import io.github.praveen7294.checkstyle.spellcheck.util.IdentifierWordSplitter;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Checks the spelling of Java identifiers against a Hunspell dictionary and whitelist.
 */
@StatelessCheck
public class IdentifierSpellingCheck extends AbstractCheck {

  /**
   * The message key for spelling violations in messages.properties.
   */
  public static final String MSG_KEY = "identifier.spelling";

  /**
   * Path to the Hunspell dictionary (.dic) file.
   */
  private String dictionaryFile;

  /**
   * Path to the Hunspell affix (.aff) file.
   */
  private String affixFile;

  /**
   * Path to the optional custom whitelist file.
   */
  private String whitelist;

  /**
   * The spellchecker instance used to validate words.
   */
  private SpellChecker spellChecker;

  /**
   * Sets the path to the Hunspell dictionary file.
   *
   * @param dictionaryFile path to the .dic file
   */
  public void setDictionaryFile(String dictionaryFile) {
    this.dictionaryFile = dictionaryFile;
  }

  /**
   * Sets the path to the Hunspell affix file.
   *
   * @param affixFile path to the .aff file
   */
  public void setAffixFile(String affixFile) {
    this.affixFile = affixFile;
  }

  /**
   * Sets the path to the optional whitelist file.
   *
   * @param whitelist path to the whitelist file
   */
  public void setWhitelist(String whitelist) {
    this.whitelist = whitelist;
  }

  /**
   * Creates a new {@code IdentifierSpellingCheck} instance.
   */
  public IdentifierSpellingCheck() {
    // no code by default
  }

  @Override
  public int[] getDefaultTokens() {
    return new int[] {
      TokenTypes.CLASS_DEF,
      TokenTypes.INTERFACE_DEF,
      TokenTypes.RECORD_DEF,
      TokenTypes.ENUM_DEF,
      TokenTypes.ENUM_CONSTANT_DEF,
      TokenTypes.VARIABLE_DEF,
      TokenTypes.METHOD_DEF,
      TokenTypes.PARAMETER_DEF,
    };
  }

  @Override
  public int[] getAcceptableTokens() {
    return getDefaultTokens();
  }

  @Override
  public int[] getRequiredTokens() {
    return new int[0];
  }

  @Override
  public void finishLocalSetup() {
    try {
      spellChecker = new SpellChecker(dictionaryFile, affixFile, whitelist);
    } catch (IOException | ParseException | IllegalArgumentException ex) {
      throw new IllegalStateException("Failed to initialize SpellChecker", ex);
    }
  }

  @Override
  public void visitToken(DetailAST ast) {
    final DetailAST identToken = ast.findFirstToken(TokenTypes.IDENT);
    if (identToken != null) {
      final String identifierName = identToken.getText();
      final List<String> words = IdentifierWordSplitter.split(identifierName);

      for (String word : words) {
        if (!spellChecker.isCorrect(word)) {
          log(identToken, MSG_KEY, identifierName, word);
        }
      }
    }
  }
}
