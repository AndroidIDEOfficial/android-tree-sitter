cmake_minimum_required(VERSION 3.22.1)

# Project name
project("android-tree-sitter")

# Include paths from tree-sitter
set(TS_INCLUDES ${TS_DIR}/lib/include)

# JNI header files
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

# android-tree-sitter header files
include_directories(${CMAKE_CURRENT_SOURCE_DIR})

# tree-sitter header files
include_directories(${TS_INCLUDES})

add_custom_target(generate-native-headers)

# Command to generate header files for native methods in Java
add_custom_command(TARGET generate-native-headers PRE_BUILD COMMAND ${CMAKE_COMMAND} -P ${CMAKE_CURRENT_SOURCE_DIR}/gen.header.cmake)

# add android-tree-sitter library
add_library(android-tree-sitter SHARED

        # tree-sitter-sources
        "${TS_DIR}/lib/src/lib.c"

        # android-tree-sitter sources
        langs.cc
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