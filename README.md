# Checkstyle Spellcheck Extension

<!-- Build & Quality Gate Badges -->
[![Build & Verify](https://github.com/Praveen7294/checkstyle-spellcheck/actions/workflows/build-and-verify.yml/badge.svg)](https://github.com/Praveen7294/checkstyle-spellcheck/actions/workflows/build-and-verify.yml)
[![Coverage](https://img.shields.io/badge/Coverage-100%25-brightgreen.svg?logo=jacoco&logoColor=white)]()
[![SpotBugs](https://img.shields.io/badge/SpotBugs-0_Bugs-brightgreen.svg)]()
[![PMD](https://img.shields.io/badge/PMD-0_Violations-brightgreen.svg)]()
[![Checkstyle Google Style](https://img.shields.io/badge/Checkstyle-Google_Style-blue.svg)](https://checkstyle.org/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.praveen7294/checkstyle-spellcheck.svg)](https://central.sonatype.com/artifact/io.github.praveen7294/checkstyle-spellcheck)
[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)]()
[![Checkstyle Core](https://img.shields.io/badge/Checkstyle-10.18%2B%20%7C%2014.x-informational.svg)](https://checkstyle.org/)
[![Lucene](https://img.shields.io/badge/Apache_Lucene-10.1.0-blue.svg?logo=apache&logoColor=white)](https://lucene.apache.org/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

> A high-performance [Checkstyle](https://checkstyle.org/) extension module that validates the spelling of Java
identifiers (classes, methods, variables, parameters, record components, enum constants, annotations) powered by
the [Apache Lucene](https://lucene.apache.org/) Hunspell dictionary engine.

Distributed as a standalone extension JAR to keep the core Checkstyle distribution lightweight while providing
zero-configuration spelling audits out of the box.

## Table of Contents

- [Quick Start](#quick-start)
- [Features](#features)
- [Build Tool Integration](#build-tool-integration)
    - [Maven](#1-maven)
    - [Gradle](#2-gradle)
    - [Command Line (CLI)](#3-command-line-cli)
- [Configuration](#configuration)
    - [Zero-Config Default](#minimal--zero-config)
    - [Project Whitelist](#standard-setup-with-project-whitelist)
    - [Custom Dictionaries](#custom-dictionary-language-support)
- [Properties Reference](#properties-reference)
- [Whitelist Syntax](#whitelist-syntax)
- [Building from Source](#building-from-source)
- [Acknowledgements & Credits](#acknowledgements--credits)
- [Contributing](#contributing)
- [License](#license)

## Quick Start

See `IdentifierSpellingCheck` in action in three simple files:

```bash
$ cat checkstyle.xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
          "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
          "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
  <module name="TreeWalker">
    <module name="IdentifierSpellingCheck"/>
  </module>
</module>

$ cat UserService.java
public class UserService {
    private String userAdress;     // violation: 'adress'

    public void printNamme() {     // violation: 'namme'
        System.out.println(userAdress);
    }
}

$ java -cp "checkstyle-all.jar;checkstyle-spellcheck-0.1.0.jar" \
    com.puppycrawl.tools.checkstyle.Main -c checkstyle.xml UserService.java
Starting audit...
[WARN] UserService.java:2:20: Identifier 'userAdress' contains misspelled word 'adress'. [IdentifierSpellingCheck]
[WARN] UserService.java:4:17: Identifier 'printNamme' contains misspelled word 'namme'. [IdentifierSpellingCheck]
Audit done.
Checkstyle ends with 2 warnings.
```

## Features

- **Zero-Config Default:** Bundles an embedded `en_US` dictionary (`en_US.aff` and `en_US.dic`). Ready to use
  immediately without downloading or configuring external dictionary files.
- **Full AST Identifier Coverage:** Spellchecks every declaration identifier in Java source:
    - `CLASS_DEF`, `INTERFACE_DEF`, `RECORD_DEF`, `ENUM_DEF`
    - `METHOD_DEF`, `VARIABLE_DEF`, `PARAMETER_DEF`
    - `RECORD_COMPONENT_DEF`, `ANNOTATION_DEF`, `ANNOTATION_FIELD_DEF`, `ENUM_CONSTANT_DEF`
- **Lexical Token Splitting Pipeline:**
    - `camelCase` & `PascalCase` (`userAddress`, `OrderService`)
    - `UPPER_SNAKE_CASE` (`MAX_BUFFER_SIZE`, `DEFAULT_TIMEOUT`)
    - Acronyms & Consecutive Uppercase (`HTTPServer`, `parseJSONResponse`, `XMLParser`)
    - Embedded Digits (`sha256Hash`, `param1`, `retry3Count`)
- **Single Whitelist Control:** Single, project-controlled whitelist for domain jargon, abbreviations, and technical
  acronyms.
- **In-Memory Word Cache:** Lock-free `ConcurrentHashMap` caches lookup results, ensuring linear $O(1)$ performance even
  across codebases with millions of lines of code.

## Build Tool Integration

### 1. Maven

Add `checkstyle-spellcheck` as a runtime dependency to your `maven-checkstyle-plugin`:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  <version>3.6.0</version>
  <dependencies>
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

### 2. Gradle

Declare the extension under the `checkstyle` dependency configuration in `build.gradle`:

```groovy
plugins {
    id 'checkstyle'
}

dependencies {
    checkstyle 'io.github.praveen7294:checkstyle-spellcheck:0.1.0'
}
```

### 3. Command Line (CLI)

Add both the Checkstyle all-in-one JAR and the spellcheck extension JAR to the Java classpath:

```bash
java -cp "checkstyle-14.4.0-all.jar;checkstyle-spellcheck-0.1.0.jar" \
  com.puppycrawl.tools.checkstyle.Main -c checkstyle.xml src/
```

## Configuration

### Minimal / Zero-Config

Uses the embedded English dictionary with zero external file requirements:

```xml
<module name="Checker">
  <module name="TreeWalker">
    <module name="IdentifierSpellingCheck"/>
  </module>
</module>
```

### Standard Setup with Project Whitelist

Approve technical abbreviations, framework acronyms, and domain-specific terms:

```xml
<module name="Checker">
  <module name="TreeWalker">
    <module name="IdentifierSpellingCheck">
      <property name="whitelist" value="config/whitelist.txt"/>
    </module>
  </module>
</module>
```

### Custom Dictionary (Language Support)

To audit non-English projects or specialized terminology (e.g., German, French, Medical):

```xml
<module name="Checker">
  <module name="TreeWalker">
    <module name="IdentifierSpellingCheck">
      <property name="dictionaryFile" value="config/dictionaries/de_DE.dic"/>
      <property name="affixFile" value="config/dictionaries/de_DE.aff"/>
      <property name="whitelist" value="config/whitelist.txt"/>
    </module>
  </module>
</module>
```

## Properties Reference

| Property | Description | Type | Default Value |
| :--- | :--- | :---: | :---: |
| `dictionaryFile` | Path to an external Hunspell `.dic` file. If omitted or blank, falls back to the embedded default English dictionary. | `String` | Embedded `en_US.dic` |
| `affixFile` | Path to an external Hunspell `.aff` file. If omitted or blank, falls back to the embedded default English affix rules. | `String` | Embedded `en_US.aff` |
| `whitelist` | Path to a project plaintext whitelist file. If omitted or blank, no custom whitelist is applied. | `String` | `null` |
| `tokens` | Tokens to audit. Defaults to all identifier declaration tokens. | `int[]` | `CLASS_DEF`, `INTERFACE_DEF`, `RECORD_DEF`, `ENUM_DEF`, `ENUM_CONSTANT_DEF`, `ANNOTATION_DEF`, `ANNOTATION_FIELD_DEF`, `RECORD_COMPONENT_DEF`, `VARIABLE_DEF`, `METHOD_DEF`, `PARAMETER_DEF` |

## Whitelist Syntax

The project whitelist is a plain UTF-8 text file (`whitelist.txt`):

* **One word per line:** Trimmed and normalized automatically.
* **Case-insensitive:** Terms match regardless of identifier casing (`dto`, `DTO`, `Dto`).
* **Comments supported:** Lines beginning with `#` or `//` are treated as comments.
* **Blank lines ignored:** Empty lines are safely skipped.

```text
# Domain & Architectural Terms
dto
dao
uuid
uri
regex

# Libraries & Tools
hunspell
checkstyle
sha256
lucene
```

## Building from Source

### Prerequisites

- **JDK 21+**
- **Maven 3.9+** (or use `./mvnw`)

```bash
# Clone the repository
git clone https://github.com/Praveen7294/checkstyle-spellcheck.git
cd checkstyle-spellcheck

# Compile, test, and verify quality gates
./mvnw clean verify
```

## Acknowledgements & Credits

This project stands on the shoulders of these industry-standard open-source projects:

- **[Apache Lucene](https://lucene.apache.org/):**
  Morphological analysis, affix rule processing, and dictionary lookup are powered
  by [Apache Lucene's Hunspell implementation](https://lucene.apache.org/core/10_1_0/analysis/common/org/apache/lucene/analysis/hunspell/package-summary.html) (
  `org.apache.lucene:lucene-analysis-common`), licensed under
  the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
- **[Checkstyle](https://checkstyle.org/):**
  The AST parsing infrastructure, token traversal engine, and modular static-analysis framework are built
  upon [Checkstyle](https://checkstyle.org/) (`com.puppycrawl.tools:checkstyle`), licensed under
  the [LGPL-2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html).
- **[Hunspell](https://hunspell.github.io/):**
  The dictionary format and morphological algorithms originate from the [Hunspell](https://hunspell.github.io/)
  spellchecker project used in LibreOffice, OpenOffice, and modern browsers.

## Contributing

Contributions, bug reports, and suggestions are welcome!

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/amazing-feature`).
3. Ensure all tests and static analysis checks pass with **100% coverage**:
   ```bash
   ./mvnw clean verify
   ```
4. Commit your changes following [Checkstyle commit conventions](https://checkstyle.org/contributing.html).
5. Open a Pull Request.

## License

This project is licensed under the [Apache License 2.0](LICENSE).