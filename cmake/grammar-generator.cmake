# target to generate grammar
add_custom_target(generate-grammar)

# Command to generate the grammar
add_custom_command(TARGET generate-grammar
        PRE_BUILD
        COMMAND ${CMAKE_COMMAND} -DTS_DIR=${TS_DIR} -P ${PROJECT_CMAKE_DIR}/grammar-generator-command.cmake)

# Set as dependency
add_dependencies(${CMAKE_PROJECT_NAME} generate-grammar)