# Checkstyle Spellcheck Extension

[![Build & Verify](https://github.com/Praveen7294/checkstyle-spellcheck/actions/workflows/build-and-verify.yml/badge.svg)](https://github.com/Praveen7294/checkstyle-spellcheck/actions/workflows/build-and-verify.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.praveen7294/checkstyle-spellcheck.svg)](https://central.sonatype.com/artifact/io.github.praveen7294/checkstyle-spellcheck)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-21%2B-orange.svg)]()

A high-performance [Checkstyle](https://checkstyle.org/) extension that validates the spelling of Java identifiers (
classes, methods, variables, parameters, records, enums, annotations) using the **Apache Lucene Hunspell** morphology
engine and a project-controlled whitelist.

Distributed as a standalone extension JAR to keep the core Checkstyle distribution lightweight while providing
zero-config spelling audits out of the box.

---

## Features

- **Zero-Configuration Default:** Bundles an embedded `en_US` dictionary (`en_US.aff` and `en_US.dic`). Works
  immediately with zero external file setup.
- **Comprehensive Identifier Coverage:** Audits all declaration AST tokens:
    - `CLASS_DEF`, `INTERFACE_DEF`, `RECORD_DEF`, `ENUM_DEF`
    - `METHOD_DEF`, `VARIABLE_DEF`, `PARAMETER_DEF`
    - `RECORD_COMPONENT_DEF`, `ANNOTATION_DEF`, `ANNOTATION_FIELD_DEF`, `ENUM_CONSTANT_DEF`
- **Smart Identifier Splitting:** Accurately splits and parses:
    - `camelCase` (`userAddress`, `printName`)
    - `PascalCase` (`UserAccount`, `OrderService`)
    - `UPPER_SNAKE_CASE` (`MAX_BUFFER_SIZE`, `DEFAULT_TIMEOUT`)
    - Acronyms & Consecutive Uppercase (`HTTPServer`, `parseJSONResponse`, `XMLParser`)
    - Embedded Numbers (`sha256Hash`, `param1`, `retry3Count`)
- **Single Whitelist Control:** Project-specific whitelist for domain jargon, abbreviations, acronyms, and technical
  terms.
- **In-Memory Word Cache:** Thread-safe `ConcurrentHashMap` cache eliminates redundant dictionary lookups across large
  multi-thousand file codebases.

---

## Quickstart & Installation

### 1. Maven (`pom.xml`)

Add `checkstyle-spellcheck` as a dependency inside the `maven-checkstyle-plugin`:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  <version>3.6.0</version>
  <dependencies>
    <!-- Add the spellcheck extension -->
    <dependency>
      <groupId>io.github.praveen7294</groupId>
      <artifactId>checkstyle-spellcheck</artifactId>
      <version>0.1.0</version>
    </dependency>
  </dependencies>
  <configuration>
    <configLocation>config/checkstyle.xml</configLocation>
  </configuration>
</plugin>
```

### 2. Gradle (`build.gradle`)

Add `checkstyle-spellcheck` to the `checkstyle` configuration:

```groovy
plugins {
    id 'checkstyle'
}

dependencies {
    checkstyle 'io.github.praveen7294:checkstyle-spellcheck:0.1.0'
}
```

### 3. Checkstyle Command Line (CLI)

Include the extension JAR on the Java classpath:

```bash
java -cp "checkstyle-14.4.0-all.jar;checkstyle-spellcheck-0.1.0.jar" \
  com.puppycrawl.tools.checkstyle.Main -c checkstyle.xml src/
```

---

## Configuration

### Minimal / Zero-Config Setup

By default, the check uses the embedded English dictionary with no custom configuration needed:

```xml
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
  <module name="TreeWalker">
    <module name="io.github.praveen7294.checkstyle.spellcheck.IdentifierSpellingCheck"/>
  </module>
</module>
```

### Standard Setup with Project Whitelist

To approve project-specific acronyms, technical abbreviations, and domain terms:

```xml
<module name="Checker">
  <module name="TreeWalker">
    <module name="io.github.praveen7294.checkstyle.spellcheck.IdentifierSpellingCheck">
      <property name="whitelist" value="config/whitelist.txt"/>
    </module>
  </module>
</module>
```

### Advanced Setup with Custom Dictionary

To use a language other than English (e.g., German, French, or medical/technical dictionaries):

```xml
<module name="Checker">
  <module name="TreeWalker">
    <module name="io.github.praveen7294.checkstyle.spellcheck.IdentifierSpellingCheck">
      <property name="dictionaryFile" value="config/dictionaries/de_DE.dic"/>
      <property name="affixFile" value="config/dictionaries/de_DE.aff"/>
      <property name="whitelist" value="config/whitelist.txt"/>
    </module>
  </module>
</module>
```

---

## Configuration Properties

| Property | Description | Type | Default Value |
| :--- | :--- | :---: | :---: |
| `dictionaryFile` | Path to a custom Hunspell `.dic` file. If omitted or blank, falls back to the embedded default English dictionary. | `String` | Embedded `en_US.dic` |
| `affixFile` | Path to a custom Hunspell `.aff` file. If omitted or blank, falls back to the embedded default English affix rules. | `String` | Embedded `en_US.aff` |
| `whitelist` | Path to a project plaintext whitelist file. If omitted or blank, no whitelist is applied. | `String` | `null` |
| `tokens` | Token types to audit. | `int[]` | All declaration tokens (`CLASS_DEF`, `METHOD_DEF`, `VARIABLE_DEF`, `PARAMETER_DEF`, etc.) |

---

## Whitelist Format (`whitelist.txt`)

The whitelist is a plain UTF-8 text file. Each line contains an approved word or acronym:

- **Case-insensitive:** Words in the whitelist match regardless of capitalization (`dto`, `DTO`, `Dto`).
- **Comments supported:** Lines starting with `#` or `//` are ignored.
- **Blank lines ignored:** Empty lines and whitespace are ignored.

Example `config/whitelist.txt`:

```text
# Domain Acronyms
dto
dao
uuid
uri
regex

# Technical terms & libraries
hunspell
checkstyle
sha256
```

---

## Example Violation Output

When an identifier contains a misspelled word, Checkstyle flags the exact line, column, identifier name, and misspelled
sub-word:

```text
[WARN] src/main/java/com/example/UserService.java:14:18: Identifier 'userAdress' contains misspelled word 'adress'. [IdentifierSpellingCheck]
[WARN] src/main/java/com/example/UserService.java:22:15: Identifier 'printNamme' contains misspelled word 'namme'. [IdentifierSpellingCheck]
[WARN] src/main/java/com/example/OrderRecord.java:9:10: Identifier 'LineRecrd' contains misspelled word 'recrd'. [IdentifierSpellingCheck]
```

---

## Building from Source

Requirements:

- JDK 21+
- Maven 3.9+ (or use the included `./mvnw`)

```bash
# Clone the repository
git clone https://github.com/Praveen7294/checkstyle-spellcheck.git
cd checkstyle-spellcheck

# Build and run full test suite with 100% coverage verification
./mvnw clean verify
```

---

## License

This project is licensed under the [Apache License 2.0](LICENSE).