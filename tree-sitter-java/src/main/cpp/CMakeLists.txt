cmake_minimum_required(VERSION 3.22.1)

project("tree-sitter-java")

# Include common configuration
include(../../../../common-config.cmake)

# This includes the header file for the parser
include_directories(grammar/src)

# target to generate grammar
add_custom_target(generate-grammar)

# Command to generate the grammar
add_custom_command(TARGET generate-grammar PRE_BUILD COMMAND ${CMAKE_COMMAND} -P ${CMAKE_CURRENT_SOURCE_DIR}/gen.grammar.cmake)

# add tree-sitter-java library
add_library(tree-sitter-java SHARED
        grammar/src/parser.c)