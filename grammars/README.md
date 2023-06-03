## Grammars for android-tree-sitter

This directory includes the tree sitter grammar submodules.

The `grammars.json` file contains information about all the grammars that are built
with `android-tree-sitter`. The structure of this file as follows :

```json5
[
  {
    // the name of the grammar
    // a directory with the same name must exist in this directory or a FileNotFoundException will be thrown
    // Gradle module name for the grammar will be in the 'tree-sitter-$name' format
    
    // for example, if the name is 'java', a directory named 'java' must exist in this directory
    // Also, Gradle module's name will be 'tree-sitter-java'
    "name": "java",
    
    // Extra C/C++ source files that will be included in the shared library
    "src.extra": [
      
      // Paths are relative to the grammar directory
      "src/scanner.c"
    ]
  }
]
```