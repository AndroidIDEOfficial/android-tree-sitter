cmake_minimum_required(VERSION 3.22.1)

project("tree-sitter-python")

# Include common configuration
include(../../../../cmake/common-config.cmake)

# This includes the header file for the parser
include_directories(grammar/src)

# add tree-sitter-java library
add_library(${CMAKE_PROJECT_NAME} SHARED
        grammar/src/parser.c
        grammar/src/scanner.cc
        tree-sitter-python.cpp)