#!/bin/bash

set -eu

color="\033[32m"
nocolor="\033[0m"
script_dir=$(realpath $(dirname $0))

printf "%sFinding java sources...%s\n" "${color}" "${nocolor}"
find "${script_dir}/../../java" -name "*.java" > sources.txt

printf "%sGenerating headers...%s\n" "${color}" "${nocolor}"
out="${script_dir}/classes"
mkdir "$out"
javac -d "${out}" -h "${script_dir}/../include" @sources.txt

printf "%s" "${color}"
rm -rf "${out}" sources.txt
printf "%s" "${nocolor}"
