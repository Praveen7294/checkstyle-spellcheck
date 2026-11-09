# Configuration Guide

`IdentifierSpellingCheck` is highly configurable, supporting custom whitelists, external dictionaries, and granular AST
token selection.

---

## Configuration Properties

| Property | Description | Type | Default Value |
| :--- | :--- | :---: | :---: |
| `dictionaryFile` | Path to a custom Hunspell `.dic` file. If omitted or blank, uses the embedded default English dictionary. | `String` | Embedded `en_US.dic` |
| `affixFile` | Path to a custom Hunspell `.aff` file. If omitted or blank, uses the embedded default English affix rules. | `String` | Embedded `en_US.aff` |
| `whitelist` | Path to a project plaintext whitelist file. If omitted or blank, no custom whitelist is applied. | `String` | `null` |
| `tokens` | Tokens to audit. Defaults to all identifier declaration tokens. | `int[]` | All 11 declaration tokens |

---

## Project Whitelist (`whitelist.txt`)

Every project uses domain-specific acronyms, technical abbreviations, and third-party library names that are not part of
standard English dictionaries.

To prevent false positives, point the `whitelist` property to a plain UTF-8 text file:

```xml
<module name="IdentifierSpellingCheck">
  <property name="whitelist" value="config/whitelist.txt"/>
</module>
```

### Whitelist Syntax Rules

* **One word per line:** Trimmed automatically.
* **Case-insensitive:** Terms match regardless of capitalization (`dto`, `DTO`, `Dto`).
* **Comments supported:** Lines starting with `#` or `//` are ignored.
* **Blank lines ignored:** Empty lines are safely skipped.

### Example `config/whitelist.txt`:

```text
# Domain & Architecture
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

---

## Custom Language / Corporate Dictionaries

To validate identifiers against a foreign language (e.g. German, French) or specialized medical/scientific dictionaries:

```xml
<module name="IdentifierSpellingCheck">
  <property name="dictionaryFile" value="config/dictionaries/de_DE.dic"/>
  <property name="affixFile" value="config/dictionaries/de_DE.aff"/>
  <property name="whitelist" value="config/whitelist.txt"/>
</module>
```

> [!NOTE]
> When providing custom dictionaries, both `dictionaryFile` and `affixFile` must be specified together.
