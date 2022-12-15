# android-tree-sitter
<a href="https://github.com/itsaky/AndroidIDE"><img src="https://androidide.com/github/img/androidide.php?part&for-the-badge"/></a><br><br> 
Android Java bindings for [tree-sitter](https://tree-sitter.github.io/tree-sitter/).

## Add to your project

![Latest GH Packages release](https://img.shields.io/github/v/release/AndroidIDEOfficial/android-tree-sitter?include_prereleases&amp;label=latest%20release)

```gradle
// main library
implementation 'io.github.itsaky:android-tree-sitter:<version>'

// tree-sitter-java
implementation 'io.github.itsaky:tree-sitter-java:<version>'

// tree-sitter-python
implementation 'io.github.itsaky:tree-sitter-python:<version>'
```

## Building

### Prerequisites

- Android NDK
- `JDK 11` or newer.
- `gcc`, `make` and `cmake` - To build everything for the host OS (required for unit tests).
- [`tree-sitter-cli`](https://github.com/tree-sitter/tree-sitter/tree/master/cli) - To build grammars. This can be installed either with `npm install tree-sitter-cli` or `cargo install tree-sitter-cli`. Read [this](https://github.com/tree-sitter/tree-sitter/tree/master/cli) for more details.

> _IMPORTANT: Building on a Linux machine is recommended._

### Get source

Clone this repo with :

```bash
git clone --recurse-submodules https://github.com/AndroidIDEOfficial/android-tree-sitter
```

### Build

A normal Gradle build (`./gradlew build`) can be executed in order to build everything for Android _and_ the host OS. To build `android-tree-sitter` and the grammars _only_ for the host OS, you can execute `buildForHost` task on appropriate subprojects.

## Examples

First, load the shared libraries somewhere in your application:

```java
    public class App {
      static {
        // main library
        System.loadLibrary("android-tree-sitter");

        // languages
        System.loadLibrary("tree-sitter-java");
        System.loadLibrary("tree-sitter-python");
      }
    }
```

Then, you can create a `TSParser`, set the language, and start parsing:

```java 
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguagePython.newInstance());
      try (TSTree tree = parser.parseString("def foo(bar, baz):\n  print(bar)\n  print(baz)", TSInputEncoding.TSInputEncodingUTF8 /*specify encoding, default is UTF-8*/)) {
        TSNode root = tree.getRootNode();
        assertEquals(1, root.getChildCount());
        assertEquals("module", root.getType());
        assertEquals(0, root.getStartByte());
        assertEquals(44, root.getEndByte());

        TSNode function = root.getChild(0);
        assertEquals("function_definition", function.getType());
        assertEquals(5, function.getChildCount());
      }
    }
```

For debugging, it can be helpful to see string representation of the tree:

```java
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguagePython.newInstance());
      try (TSTree tree = parser.parseString("print(\"hi\")")) {
        assertEquals(
          "(module (expression_statement (call function: (identifier) arguments: (argument_list (string)))))",
          tree.getRootNode().getNodeString()
        );
      }
    }
```

If you're going to be traversing a tree, then you can use the `walk` method, which is much more efficient than the above getters:

```java
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguagePython.newInstance());
      try (TSTree tree = parser.parseString("def foo(bar, baz):\n  print(bar)\n  print(baz)")) {
        try (TSTreeCursor cursor = tree.getRootNode().walk()) {
          assertEquals("module", cursor.getCurrentTreeCursorNode().getType());
          cursor.gotoFirstChild();
          assertEquals("function_definition", cursor.getCurrentTreeCursorNode().getType());
          cursor.gotoFirstChild();
          assertEquals("def", cursor.getCurrentTreeCursorNode().getType());
          cursor.gotoNextSibling();
          cursor.gotoParent();
        }
      }
```

For more examples, see the tests in `android-tree-sitter/src/test/java/com/itsaky/androidide/treesitter`.
