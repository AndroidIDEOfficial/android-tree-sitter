#!/usr/bin/env python3

import argparse
import ctypes.util
import distutils.ccompiler
import os
import platform
import sys
import tempfile
from tty import CC


# adapted from https://github.com/tree-sitter/py-tree-sitter
def build(args):
    repositories = args.repositories
    output_path = args.output if args.output else "libts"
    arch = args.arch if args.arch else None
    verbose = args.verbose if args.verbose else False
    ndk_dir = args.ndk
    min_sdk = args.min_sdk

    if not ndk_dir :
        raise ValueError("No value specified for --ndk")

    host = platform.system().lower() + "-x86_64"
    clang_name_prefix = arch if not arch == "arm" else "armv7a"
    clang_name_suffix = "" if not arch == "arm" else "eabi"
    clang_name = f"{clang_name_prefix}-linux-android{min_sdk}{clang_name_suffix}"
    bin_dir = f"{ndk_dir}/toolchains/llvm/prebuilt/{host}/bin"
    cc = f"{bin_dir}/{clang_name}-clang"
    cxx = f"{cc}++"
    lld = f"{bin_dir}/lld"

    print(f"Using CC:", cc)
    print(f"Using CXX:", cxx)
    print(f"Using LD:", lld)    

    env = f"CC='{cc}'"
    env += f" CXX='{cxx}'"
    env += f" LD={lld}"

    if arch and platform.system() != "Darwin":
        arch = "64" if "64" in arch else "32"

    output_path = f"{output_path}.{'dylib' if platform.system() == 'Darwin' else 'so'}"
    here = os.path.dirname(os.path.realpath(__file__))
    if arch:
        env += (
            f"CFLAGS='-arch {arch} -mmacosx-version-min=11.0' LDFLAGS='-arch {arch}'"
            if platform.system() == "Darwin"
            else f"CFLAGS='-m{arch}' LDFLAGS='-m{arch}'"
        )

    os.system(
        f"make -C {os.path.join(here, 'tree-sitter')} clean {'> /dev/null' if not verbose else ''}"
    )
    os.system(
        f"{env} make -C {os.path.join(here, 'tree-sitter')} {'> /dev/null' if not verbose else ''}"
    )

    cpp = False
    source_paths = [
        os.path.join(here, "lib", "ts.cc"),
        os.path.join(here, "lib", "langs.cc"),
    ]

    #os.environ["CC"] = cc
    #os.environ["CXX"] = cxx
    #os.environ["LD"] = lld
    CC=cc
    compiler = distutils.ccompiler.new_compiler(compiler=cc)
    for repository in repositories:
        src_path = os.path.join(repository, "src")
        source_paths.append(os.path.join(src_path, "parser.c"))
        scanner_c = os.path.join(src_path, "scanner.c")
        scanner_cc = os.path.join(src_path, "scanner.cc")
        if os.path.exists(scanner_cc):
            cpp = True
            source_paths.append(scanner_cc)
        elif os.path.exists(scanner_c):
            source_paths.append(scanner_c)

        compiler.define_macro(
            f"TS_LANGUAGE_{os.path.split(repository.rstrip('/'))[1].split('tree-sitter-')[-1].replace('-', '_').upper()}",
            "1",
        )

    source_mtimes = [os.path.getmtime(
        __file__)] + [os.path.getmtime(path) for path in source_paths]
    if cpp:
        if ctypes.util.find_library("stdc++"):
            compiler.add_library("stdc++")
        elif ctypes.util.find_library("c++"):
            compiler.add_library("c++")

    output_mtime = os.path.getmtime(
        output_path) if os.path.exists(output_path) else 0
    if max(source_mtimes) <= output_mtime:
        return False

    with tempfile.TemporaryDirectory(suffix="tree_sitter_language") as out_dir:
        object_paths = []
        for source_path in source_paths:
            flags = ["-O3"]

            if platform.system() == "Linux":
                flags.append("-fPIC")

            if source_path.endswith(".c"):
                flags.append("-std=c99")

            if arch:
                flags += ["-arch",
                          arch] if platform.system() == "Darwin" else [f"-m{arch}"]

            include_dirs = [
                os.path.dirname(source_path),
                os.path.join(here, "tree-sitter", "lib", "include"),
                os.path.join(os.environ["JAVA_HOME"], "include"),
            ]

            if platform.system() == "Linux":
                include_dirs.append(os.path.join(
                    os.environ["JAVA_HOME"], "include", "linux"))
            elif platform.system() == "Darwin":
                include_dirs.append(os.path.join(
                    os.environ["JAVA_HOME"], "include", "darwin"))

            object_paths.append(
                compiler.compile(
                    [source_path],
                    output_dir=out_dir,
                    include_dirs=include_dirs,
                    extra_preargs=flags,
                )[0]
            )

        extra_preargs = []
        if platform.system() == "Darwin":
            extra_preargs.append("-dynamiclib")

        if arch:
            extra_preargs += ["-arch",
                              arch] if platform.system() == "Darwin" else [f"-m{arch}"]

        compiler.link_shared_object(
            object_paths,
            output_path,
            extra_preargs=extra_preargs,
            extra_postargs=[os.path.join(
                here, "tree-sitter", "libtree-sitter.a")],
            library_dirs=[os.path.join(here, "tree-sitter")],
        )

    return True


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Build a tree-sitter library")
    parser.add_argument(
        "-a",
        "--arch",
        choices=["aarch64", "arm", "x86", "x86_64"],
        help="Architecture to build for.",
    )
    parser.add_argument(
        "-o", "--output", default="libjava-tree-sitter", help="Output file name")
    parser.add_argument("-v", "--verbose", action="store_true",
                        help="Print verbose output")
    parser.add_argument("-n", "--ndk", required=True,
                        help="Path to the Android NDK.")
    parser.add_argument("-m", "--min-sdk", help="Min SDK version for the generated shared library", default="21")
    parser.add_argument(
        "repositories",
        nargs="+",
        help="tree-sitter repositories to include in build",
    )

    args = parser.parse_args()
    distutils.log.set_verbosity(int(args.verbose))
    build(args)
