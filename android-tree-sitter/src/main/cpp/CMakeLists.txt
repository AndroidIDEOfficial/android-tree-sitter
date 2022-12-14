cmake_minimum_required(VERSION 3.22.1)

# Project name
project("android-tree-sitter")

# If not building for Android, we need to include header files from JDK
if (NOT ${CMAKE_SYSTEM_NAME} STREQUAL Android)
    find_package(JNI REQUIRED)

    # Path for jni.h
    include_directories(${JAVA_INCLUDE_PATH})

    # Path for jni_md.h
    include_directories(${JAVA_INCLUDE_PATH2})
endif ()

# If not building with Gradle, these variables won't be set
if (NOT DEFINED PROJECT_DIR)
    set(PROJECT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../../")
endif ()
if (NOT DEFINED TS_DIR)
    set(TS_DIR "${PROJECT_DIR}/tree-sitter-lib")
endif ()

# Include paths from tree-sitter
set(TS_INCLUDES ${TS_DIR}/lib/include ${TS_DIR}/lib/src)

# JNI header files
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

# android-tree-sitter header files
include_directories(${CMAKE_CURRENT_SOURCE_DIR})

# tree-sitter header files
include_directories(${TS_INCLUDES})

# target to generate header files
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