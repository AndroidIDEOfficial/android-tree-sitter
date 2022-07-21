# android-tree-sitter
Android Java bindings for [tree-sitter](https://tree-sitter.github.io/tree-sitter/).

`android-tree-sitter` is adapted from [jakobkhansen/java-tree-sitter](https://github.com/jakobkhansen/java-tree-sitter) which is a fork of [serenadeai/java-tree-sitter](https://github.com/serenadeai/java-tree-sitter).

## Building

- `git clone` this repo.
- Init/update submodules with `git submodule update --init`
- If building from Android Studio, set the `ndk.dir` and `java.home` properties in your `local.properties` file.

### Use the `build.sh` script

```
usage: ./build.sh [-h] [-a {aarch64,arm,x86,x86_64}] [-o OUTPUT] [-v] -n NDK [-m MIN_SDK] grammars [grammars ...]

Build a tree-sitter library

positional arguments:
  grammars          tree-sitter repositories to include in build

options:
  -h,              Show this help message and exit
  -a               Architecture to build for {aarch64,arm,x86,x86_64}.
  -o OUTPUT        Output file name (OUTPUT.so)
  -n NDK           Path to the Android NDK.
  -m               Min SDK version for the generated shared library
  -s               Build for the host OS. If this option is set, all other options are not used.
```

For example, the following command builds the shared library for `arm64-v8a` Android 8 and above with  `tree-sitter-java` and `tree-sitter-python` grammars :

```
./build.sh -a aarch64 -m 26 -n <path_to_ndk> java python
```

### Build with Gradle tasks

You can execute the following Gradle tasks to build the shared library.

- `buildSharedObjectForAarch64` - Build the shared library for `aarch64`.
- `buildSharedObjectForArm` - Build the shared library for `arm` (or `armeabi-v7a`).
- `buildSharedObjectForX86` - Build the shared library for `x86` (or `i686`).
- `buildSharedObjectForX86_64` - Build the shared library for `x86_64`.
- `buildSharedObjectForHost` - Build the shared library for the host OS. Helpful to testing.
- `buildSharedObjectForAll` - Executes all of the above tasks.

## Adding more grammars

You could either add the submodule for the grammar with `git submodule add [..]` or manually clone the grammar repositories in the `grammars` folder.

The name of the grammar repositories must be in the format `tree-sitter-LANG` where `LANG` is the name of the language.

## Examples

First, load the shared object somewhere in your application:

```java
    public class App {
      static {
        System.load("<soname>"); // <soname> will be 'ts' for 'libts.so'
      }
    }
```

Then, you can create a `Parser`, set the language, and parse a string:

```java 
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.python());
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

For debugging, it can be helpful to see a string of the tree:

```java
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.python());
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
      parser.setLanguage(TSLanguages.python());
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

For more examples, see the tests in `src/test/java/com/itsaky/androidide/treesitter`.
