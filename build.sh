#!/bin/bash

set -eu

print_help() {
  echo """
usage: ./build.sh [-h] [-a {aarch64,arm,x86,x86_64}] [-o OUTPUT] [-v] -n NDK [-m MIN_SDK] repositories [grammars ...]

Build a tree-sitter library

positional arguments:
  grammars          tree-sitter repositories to include in build

options:
  -h,              Show this help message and exit
  -a               Architecture to build for {aarch64,arm,x86,x86_64}.
  -o OUTPU         Output file name (OUTPUT.so)
  -n NDK           Path to the Android NDK.
  -m               Min SDK version for the generated shared librar
    """
}

arch="aarch64"
soname="libts"
min_sdk="24"
ndk_dir=""

OPTIND=1
while getopts "h?a:o:m:n:" opt; do
  case "$opt" in
    h|\?)
      print_help
      exit 0
      ;;
    a) arch=$OPTARG
      ;;
    o) soname=$OPTARG
      ;;
    m) min_sdk=$OPTARG
      ;;
    n) ndk_dir=$OPTARG
      ;;
  esac
done

shift $((OPTIND-1))
[ "${1:-}" = "--" ] && shift

system=$(uname -s)
system=$(echo ${system,,})
ndk_host="$system-x86_64"
ndk_bin="$ndk_dir/toolchains/llvm/prebuilt/$ndk_host/bin"
arch_bits="32"
cc_prefix=$arch
cc_suffix=""

if [ "$arch" == "arm" ]; then
  cc_prefix="armv7a"
  cc_suffix="eabi"
elif [ "$arch" == "x86" ]; then
  cc_prefix="i686"
fi

if [ "$arch" == *"64"* ]; then
  arch_bits="64"
fi

clang_qualifier="${cc_prefix}-linux-android${cc_suffix}${min_sdk}"
export CC="${ndk_bin}/${clang_qualifier}-clang"
export CXX="${CC}++"

echo "ARCH      : $arch"
echo "OUTPUT    : $soname"
echo "MIN SDK   : $min_sdk"
echo "NDK DIR   : $ndk_dir"
echo "Grammars  : $*"
echo ""
echo "Building tree-sitter..."

make -C "./tree-sitter" clean
make -C "./tree-sitter"

echo ""
echo "Building grammars..."

dest_dir="output/${clang_qualifier}"
dest_so="${soname}.so"

mkdir -p $dest_dir

macros=""
sources="lib/ts.cc"
sources+=" lib/langs.cc"
iscpp=""
for lang in $*
do
  dir="grammars/tree-sitter-$lang"
  src="$dir/src"
  sources+=" $src/parser.c"
  scanner_c="$src/scanner.c"
  scanner_cc="$src/scanner.cc"

  if [ -f "$scanner_cc" ]; then
    iscpp="1"
    sources+=" $scanner_cc"
  elif [ -f "$scanner_c" ]; then
    sources+="$scanner_c"
  fi

  g_LANG=$(echo ${lang^^})
  macros+=" -DTS_LANGUAGE_${g_LANG}=1"
done

echo "$sources"
echo "$macros"

objects=""
includes="-I./tree-sitter/lib/include -I./tree-sitter/lib/src"
out_dir_base="./output/${clang_qualifier}"

rm -rvf $out_dir_base

IFS=' ' read -ra SOURCES <<< "$sources"
for source in "${SOURCES[@]}"; do
  flags="-fPIC -L./tree-sitter -ltree-sitter -shared"
  if [ "$source" == *".c" ]; then
    flags+="-std=c99"
  fi

  dir_name=$(dirname $source)
  out_dir="${out_dir_base}/$dir_name"

  mkdir -p $out_dir

  obj_name=$(basename $source)
  obj_name=${obj_name/.c*/.a}
  
  obj="${out_dir}/${obj_name}"
  objects+=" $obj"
  $CC $flags $includes -o "$obj" $source
done

$CXX -shared -fPIC -o "${out_dir_base}/${soname}.so" $objects