#!/bin/bash

set -eu

script_dir=$(realpath $(dirname $0))

for header in TreeSitter TSLanguages TSNode TSLanguage.TSLanguageNative
do
  javah -d $script_dir/lib -classpath ${script_dir}/android-tree-sitter/src/main/java com.itsaky.androidide.treesitter.${header}
done
