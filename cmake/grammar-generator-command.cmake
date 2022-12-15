

# Set the 'tree-sitter' command to use
set(TS_CMD "${TS_DIR}/cli/build/release/tree-sitter")
if ("$ENV{TS_CLI_BUILD_FROM_SOURCE}" STREQUAL "false")
    message(WARNING "Using pre-installed 'tree-sitter' command to build grammars")
    set(TS_CMD tree-sitter)
endif ()

message("" "Using command '${TS_CMD}' to build ${CMAKE_PROJECT_NAME} grammar")

# Generate header files for native methods
execute_process(
        COMMAND ${TS_CMD} generate
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/grammar
)