#!/bin/bash

set -eu

script_dir=$(realpath $(dirname $0))

print_help() {
  echo """
usage: ${script_dir}/build.sh [-h] [-a {aarch64,arm,x86,x86_64}] [-o OUTPUT] [-v] -n NDK [-m MIN_SDK] grammars [grammars ...]

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
    """
}

arch="aarch64"
soname="libts"
min_sdk="24"
ndk_dir=""
verbose_out=false

OPTIND=1
while getopts "h?sva:o:m:n:" opt; do
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
    s) for_host="true"
      ;;
    v) verbose_out=true
      ;;
  esac
done

shift $((OPTIND-1))
[ "${1:-}" = "--" ] && shift

verbose() {
  if [ "$verbose_out" = true ]; then
    echo $@
  fi
}

system=$(uname -s)
system=$(echo "${system,,}")
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
strp="${ndk_bin}/llvm-strip"
export CC="${ndk_bin}/${clang_qualifier}-clang"
export CXX="${CC}++"

if [ ! -z "${for_host+x}" ]; then
  clang_qualifier="host"
  strp="strip"
  export CC="gcc"
  export CXX="g++"
  echo "Building for host..."
  echo ""
else
  echo "ARCH      : $arch"
  echo "OUTPUT    : $soname"
  echo "MIN SDK   : $min_sdk"
  echo "NDK DIR   : $ndk_dir"
  echo "Grammars  : $*"  
  echo ""
  echo "Building for Android..."
  echo ""
fi

echo "Building tree-sitter..."

make -C "${script_dir}/tree-sitter" clean
make -C "${script_dir}/tree-sitter"

echo ""
echo "Building grammars..."

dest_dir="${script_dir}/output/${clang_qualifier}"
dest_so="${soname}.so"

mkdir -p $dest_dir

macros=""
sources=""
for file in ${script_dir}/lib/*; do
  if [[ $file == *.h || -d $file ]]; then
    verbose "Skiping $file"
    continue
  fi
  sources+=${file}
  sources+=" "
done

iscpp=""
for lang in $*
do
  dir="${script_dir}/grammars/tree-sitter-$lang"
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

verbose
verbose "Sources --------------------------------"
for source in $sources; do
  verbose $source
done
verbose ""
verbose "Macros --------------------------------"
for macro in $macros; do
  verbose $macro
done
verbose ""

objects=""
includes="-I${script_dir}/tree-sitter/lib/include -I${script_dir}/tree-sitter/lib/src -I${script_dir}/lib/include"
out_dir_base="${script_dir}/output/${clang_qualifier}"

if [ ! -z "${for_host+x}" ]; then
  includes+=" -I$JAVA_HOME/include -I$JAVA_HOME/include/${system}"
fi

rm -rvf $out_dir_base

IFS=' ' read -ra SOURCES <<< "$sources"
for source in "${SOURCES[@]}"; do
  flags="-fPIC -c"
  if [ "$source" == *".c" ]; then
    flags+="-std=c99"
  fi

  dir_name=$(dirname $source)
  dir_name=${dir_name##*$script_dir}
  out_dir="${out_dir_base}$dir_name"

  mkdir -p $out_dir

  obj_name=$(basename $source)
  obj_name=${obj_name/.c*/.a}
  
  obj="${out_dir}/${obj_name}"
  objects+=" $obj"

  cmd="$CC $flags $includes $macros -o "$obj" $source"
  echo "Compiling $source"
  verbose $cmd
  $cmd
  verbose "Stripping debug symbols from: $obj"

  if [ ! -z "${for_host+x}" ]; then
    strip -g $obj
  else
    $strp -g $obj
  fi
  verbose ""
done


so="${out_dir_base}/${soname}.so"
cmd="$CXX -shared -fPIC -static-libstdc++ -o $so $objects ${script_dir}/tree-sitter/libtree-sitter.a"
verbose $cmd
$cmd

if [ ! -z "${for_host+x}" ]; then
  strip -g $so
else
  $strp -g $so
fi

if [ -z "${for_host+x}" ]; then
  # Copy the generated libraries to appropriate jniLibs subdirectories
  out_dir=$arch

  if [ "$out_dir" == "aarch64" ]; then
    out_dir="arm64-v8a"
  fi
  if [ "$out_dir" == "arm" ]; then
    out_dir="armeabi-v7a"
  fi

  out_dir="${script_dir}/android-tree-sitter/src/main/jniLibs/$out_dir/"
  mkdir -p $out_dir
  cp $so $out_dir
fi

echo ""
echo "-------------- Build finished ----------------"
echo ""
