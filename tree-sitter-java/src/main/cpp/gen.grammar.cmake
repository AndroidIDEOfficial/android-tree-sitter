# Generate header files for native methods
execute_process (
        COMMAND tree_sitter generate
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/grammar
)