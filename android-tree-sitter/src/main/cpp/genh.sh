#!/bin/bash

set -eu

color="\033[32m"
nocolor="\033[0m"
script_dir=$(realpath $(dirname $0))

printf "${color}Finding java sources...${nocolor}\n"
find ${script_dir}/android-tree-sitter/src/main/java -name "*.java" > sources.txt

printf "${color}Generating headers...${nocolor}\n"
out="${script_dir}/classes"
mkdir $out
javac -d ${out} -h ${script_dir}/lib/include @sources.txt

printf "${color}"
rm -rf ${out} sources.txt
printf "${nocolor}"
