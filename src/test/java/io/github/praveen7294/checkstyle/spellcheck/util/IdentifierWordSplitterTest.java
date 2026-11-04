package io.github.praveen7294.checkstyle.spellcheck.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link IdentifierWordSplitter} verifying 100% instruction and branch coverage.
 */
public class IdentifierWordSplitterTest {

  @Test
  public void testHasPrivateConstructor() throws Exception {
    final Constructor<IdentifierWordSplitter> constructor = IdentifierWordSplitter.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor should be private");
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  public void testNullAndBlankIdentifiers() {
    // Tests identifier == null
    assertTrue(IdentifierWordSplitter.split(null).isEmpty());

    // Tests identifier.isBlank() with empty string
    assertTrue(IdentifierWordSplitter.split("").isEmpty());

    // Tests identifier.isBlank() with whitespace string
    assertTrue(IdentifierWordSplitter.split("   ").isEmpty());
  }

  @Test
  public void testSingleWord() {
    assertEquals(List.of("user"), IdentifierWordSplitter.split("user"));
    assertEquals(List.of("name"), IdentifierWordSplitter.split("name"));
  }

  @Test
  public void testCamelCaseIdentifiers() {
    assertEquals(List.of("user", "name"), IdentifierWordSplitter.split("userName"));
    assertEquals(List.of("get", "line", "number"), IdentifierWordSplitter.split("getLineNumber"));
    assertEquals(List.of("file", "lines"), IdentifierWordSplitter.split("fileLines"));
    assertEquals(List.of("column", "number"), IdentifierWordSplitter.split("columnNumber"));
  }

  @Test
  public void testResultIsUnmodifiable() {
    final List<String> result = IdentifierWordSplitter.split("userName");
    assertThrows(UnsupportedOperationException.class, () ->
      result.add("extraWord"));
  }

  @Test
  public void testPascalCaseIdentifiers() {
    assertEquals(List.of("abstract", "check"), IdentifierWordSplitter.split("AbstractCheck"));
    assertEquals(List.of("user", "account", "service"), IdentifierWordSplitter.split("UserAccountService"));
    assertEquals(List.of("line", "number"), IdentifierWordSplitter.split("LineNumber"));
  }

  @Test
  public void testUpperSnakeCaseIdentifiers() {
    assertEquals(List.of("max", "buffer", "size"), IdentifierWordSplitter.split("MAX_BUFFER_SIZE"));
    assertEquals(List.of("default", "timeout"), IdentifierWordSplitter.split("DEFAULT_TIMEOUT"));
    assertEquals(List.of("timeout"), IdentifierWordSplitter.split("TIMEOUT"));
    assertEquals(List.of("internal", "id"), IdentifierWordSplitter.split("_INTERNAL_ID_"));
    assertEquals(List.of("key"), IdentifierWordSplitter.split("__KEY__"));
    assertTrue(IdentifierWordSplitter.split("_").isEmpty());
    assertTrue(IdentifierWordSplitter.split("___").isEmpty());
  }

  @Test
  public void testMixedSnakeAndCamelCase() {
    assertEquals(List.of("user", "name"), IdentifierWordSplitter.split("user_name"));
    assertEquals(List.of("user", "account", "name"), IdentifierWordSplitter.split("user_accountName"));
    assertEquals(List.of("default", "user", "timeout"), IdentifierWordSplitter.split("DEFAULT_userTimeout"));
  }

  @Test
  public void testAcronymIdentifiers() {
    assertEquals(List.of("http", "server"), IdentifierWordSplitter.split("HTTPServer"));
    assertEquals(List.of("parse", "json", "response"), IdentifierWordSplitter.split("parseJSONResponse"));
    assertEquals(List.of("url", "class", "loader"), IdentifierWordSplitter.split("URLClassLoader"));
    assertEquals(List.of("io", "error"), IdentifierWordSplitter.split("IOError"));
    assertEquals(List.of("get", "user", "id"), IdentifierWordSplitter.split("getUserID"));
    assertEquals(List.of("user", "dto"), IdentifierWordSplitter.split("UserDTO"));
    assertEquals(List.of("html"), IdentifierWordSplitter.split("HTML"));
  }

  @Test
  public void testIdentifiersWithDigits() {
    assertEquals(List.of("version", "number"), IdentifierWordSplitter.split("version2Number"));
    assertEquals(List.of("sha", "hash"), IdentifierWordSplitter.split("sha256Hash"));
    assertEquals(List.of("user", "address"), IdentifierWordSplitter.split("user1Address"));
    assertEquals(List.of("param"), IdentifierWordSplitter.split("param1"));
    assertEquals(List.of("arg"), IdentifierWordSplitter.split("arg0"));
    assertEquals(List.of("max", "retry", "count"), IdentifierWordSplitter.split("MAX_RETRY_3_COUNT"));
    assertEquals(List.of("base", "encoded"), IdentifierWordSplitter.split("base64Encoded"));
    assertTrue(IdentifierWordSplitter.split("123").isEmpty());
    assertTrue(IdentifierWordSplitter.split("_123_").isEmpty());
  }
}
