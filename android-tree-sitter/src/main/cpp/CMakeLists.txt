cmake_minimum_required(VERSION 3.22.1)

# Project name
project("android-tree-sitter")

# Include common configuration
include(../../../../cmake/common-config.cmake)

# add android-tree-sitter library
add_library(android-tree-sitter SHARED

        # tree-sitter-sources
        "${TS_DIR}/lib/src/lib.c"

        # android-tree-sitter sources
        parser.cc
        ts.cc
        ts_cursor.cc
        ts_language.cc
        ts_node.cc
        ts_parser.cc
        ts_query.cc
        ts_query_cursor.cc
        ts_tree.cc
        cache/StrCache.cpp
        utf16str/JavaUTF16String.cpp
        utf16str/JavaUTF16StringFactory.cpp
        utf16str/UTF16String.cpp
        utils/jni_string.cpp
        utils/jni_utils.cpp
        utils/ts_obj_utils.cc
        utils/utils.cpp)