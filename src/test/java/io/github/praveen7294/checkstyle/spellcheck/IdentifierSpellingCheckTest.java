package io.github.praveen7294.checkstyle.spellcheck;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Unit tests for {@link IdentifierSpellingCheck} verifying 100% instruction and branch coverage.
 */
public class IdentifierSpellingCheckTest extends AbstractModuleTestSupport {

  private static final String DIC_PATH = "src/test/resources/dictionaries/en_US.dic";
  private static final String AFF_PATH = "src/test/resources/dictionaries/en_US.aff";
  private static final String WHITELIST_PATH = "src/test/resources/dictionaries/whitelist.txt";

  @Override
  public String getPackageLocation() {
    return "io/github/praveen7294/checkstyle/spellcheck";
  }

  @Test
  public void testCamelCaseIdentifiers() throws Exception {
    final DefaultConfiguration checkConfig = createModuleConfig(IdentifierSpellingCheck.class);
    checkConfig.addProperty("dictionaryFile", DIC_PATH);
    checkConfig.addProperty("affixFile", AFF_PATH);
    checkConfig.addProperty("whitelist", WHITELIST_PATH);

    final String[] expected = {
      "9:18: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "userAdress", "adress"),
      "15:15: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "printNamme", "namme"),
    };

    verify(checkConfig, getPath("InputIdentifierSpellingCamelCase.java"), expected);
  }

  @Test
  public void testTokens() {
    final IdentifierSpellingCheck check = new IdentifierSpellingCheck();
    final int[] expectedDefaultTokens = {
      TokenTypes.CLASS_DEF,
      TokenTypes.INTERFACE_DEF,
      TokenTypes.RECORD_DEF,
      TokenTypes.ENUM_DEF,
      TokenTypes.ENUM_CONSTANT_DEF,
      TokenTypes.ANNOTATION_DEF,
      TokenTypes.ANNOTATION_FIELD_DEF,
      TokenTypes.RECORD_COMPONENT_DEF,
      TokenTypes.VARIABLE_DEF,
      TokenTypes.METHOD_DEF,
      TokenTypes.PARAMETER_DEF,
    };
    assertArrayEquals(expectedDefaultTokens, check.getDefaultTokens());
    assertArrayEquals(expectedDefaultTokens, check.getAcceptableTokens());
    assertArrayEquals(new int[0], check.getRequiredTokens());
  }

  @Test
  public void testFinishLocalSetupFailsOnInvalidFiles() {
    final IdentifierSpellingCheck check = new IdentifierSpellingCheck();
    // Non-existent custom dictionary files should cause finishLocalSetup to fail
    check.setDictionaryFile("non_existent.dic");
    check.setAffixFile("non_existent.aff");
    assertThrows(IllegalStateException.class, check::finishLocalSetup);
  }

  @Test
  public void testVisitTokenWithoutIdentChild() {
    final IdentifierSpellingCheck check = new IdentifierSpellingCheck();
    check.setDictionaryFile(DIC_PATH);
    check.setAffixFile(AFF_PATH);
    check.setWhitelist(WHITELIST_PATH);
    check.finishLocalSetup();

    // AST node without an IDENT child (covers identToken == null branch)
    final DetailAstImpl astWithoutIdent = new DetailAstImpl();
    astWithoutIdent.setType(TokenTypes.VARIABLE_DEF);
    check.visitToken(astWithoutIdent);
  }

  @Test
  public void testPascalCaseIdentifiers() throws Exception {
    final DefaultConfiguration checkConfig = createModuleConfig(IdentifierSpellingCheck.class);
    checkConfig.addProperty("dictionaryFile", DIC_PATH);
    checkConfig.addProperty("affixFile", AFF_PATH);
    checkConfig.addProperty("whitelist", WHITELIST_PATH);

    final String[] expected = {
      "19:9: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "UserAcount", "acount"),
      "22:13: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "NammeAuditor", "namme"),
      "25:10: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "LineRecrd", "recrd"),
    };

    verify(checkConfig, getPath("InputIdentifierSpellingPascalCase.java"), expected);
  }

  @Test
  public void testConstantAndEnumIdentifiers() throws Exception {
    final DefaultConfiguration checkConfig = createModuleConfig(IdentifierSpellingCheck.class);
    checkConfig.addProperty("dictionaryFile", DIC_PATH);
    checkConfig.addProperty("affixFile", AFF_PATH);
    checkConfig.addProperty("whitelist", WHITELIST_PATH);

    final String[] expected = {
      "13:27: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "MAXX_BUFFER_SIZE", "maxx"),
      "14:30: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "DEFAULLT_NAME", "defaullt"),
      "25:8: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "Seazon", "seazon"),
      "26:5: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "SPRINNG", "sprinng"),
    };

    verify(checkConfig, getPath("InputIdentifierSpellingConstantAndEnum.java"), expected);
  }

  @Test
  public void testAcronymIdentifiers() throws Exception {
    final DefaultConfiguration checkConfig = createModuleConfig(IdentifierSpellingCheck.class);
    checkConfig.addProperty("dictionaryFile", DIC_PATH);
    checkConfig.addProperty("affixFile", AFF_PATH);
    checkConfig.addProperty("whitelist", WHITELIST_PATH);

    final String[] expected = {
      "24:9: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "HTPPServer", "htpp"),
      "27:9: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "HTTPServver", "servver"),
      "30:15: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "parseJSONResponsse", "responsse"),
    };

    verify(checkConfig, getPath("InputIdentifierSpellingAcronyms.java"), expected);
  }

  @Test
  public void testIdentifiersWithDigits() throws Exception {
    final DefaultConfiguration checkConfig = createModuleConfig(IdentifierSpellingCheck.class);
    checkConfig.addProperty("dictionaryFile", DIC_PATH);
    checkConfig.addProperty("affixFile", AFF_PATH);
    checkConfig.addProperty("whitelist", WHITELIST_PATH);

    final String[] expected = {
      "16:18: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "user1Namme", "namme"),
      "17:15: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "maxxRetry3Count", "maxx"),
      "19:15: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "processSha256Hashe", "hashe"),
      "19:41: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "paramm1", "paramm"),
    };

    verify(checkConfig, getPath("InputIdentifierSpellingNumbers.java"), expected);
  }

  @Test
  public void testAnnotationsAndRecordComponents() throws Exception {
    final DefaultConfiguration checkConfig = createModuleConfig(IdentifierSpellingCheck.class);
    checkConfig.addProperty("dictionaryFile", DIC_PATH);
    checkConfig.addProperty("affixFile", AFF_PATH);
    checkConfig.addProperty("whitelist", WHITELIST_PATH);

    final String[] expected = {
      "16:14: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "ServviceAuditor", "servvice"),
      "17:12: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "fileePath", "filee"),
      "19:9: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "retryyCount", "retryy"),
      "27:31: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "userNamme", "namme"),
      "27:46: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "userAgge", "agge"),
    };

    verify(checkConfig, getPath("InputIdentifierSpellingAnnotationsAndRecords.java"), expected);
  }

  @Test
  public void testZeroConfigEmbeddedDictionary() throws Exception {
    final DefaultConfiguration checkConfig = createModuleConfig(IdentifierSpellingCheck.class);
    // Notice: NO dictionaryFile or affixFile configured! Only whitelist!
    checkConfig.addProperty("whitelist", WHITELIST_PATH);

    final String[] expected = {
      "9:18: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "userAdress", "adress"),
      "15:15: " + getCheckMessage(IdentifierSpellingCheck.MSG_KEY, "printNamme", "namme"),
    };

    verify(checkConfig, getPath("InputIdentifierSpellingCamelCase.java"), expected);
  }
}
