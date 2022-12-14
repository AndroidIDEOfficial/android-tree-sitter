cmake_minimum_required(VERSION 3.22.1)

# Project name
project("android-tree-sitter")

# Include common configuration
include(../../../../common-config.cmake)

# android-tree-sitter header files
include_directories(${CMAKE_CURRENT_SOURCE_DIR})

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
        utils.cc)