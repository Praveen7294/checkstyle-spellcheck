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
    final Constructor<IdentifierWordSplitter> constructor =
      IdentifierWordSplitter.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()),
      "Constructor should be private");
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
    assertEquals(List.of("user"),
      IdentifierWordSplitter.split("user"));
    assertEquals(List.of("name"),
      IdentifierWordSplitter.split("name"));
  }

  @Test
  public void testCamelCaseIdentifiers() {
    assertEquals(List.of("user", "name"),
      IdentifierWordSplitter.split("userName"));
    assertEquals(List.of("get", "line", "number"),
      IdentifierWordSplitter.split("getLineNumber"));
    assertEquals(List.of("file", "lines"),
      IdentifierWordSplitter.split("fileLines"));
    assertEquals(List.of("column", "number"),
      IdentifierWordSplitter.split("columnNumber"));
  }

  @Test
  public void testResultIsUnmodifiable() {
    final List<String> result = IdentifierWordSplitter.split("userName");
    assertThrows(UnsupportedOperationException.class, () ->
      result.add("extraWord"));
  }
}
