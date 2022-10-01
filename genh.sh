#!/bin/bash

set -eu

script_dir=$(realpath $(dirname $0))

for header in TreeSitter TSLanguages.Native TSLanguage.Native TSParser.Native TSTreeCursor.Native TSTree.Native TSNode
do
  javah -d $script_dir/lib -classpath ${script_dir}/android-tree-sitter/src/main/java com.itsaky.androidide.treesitter.${header}
done
