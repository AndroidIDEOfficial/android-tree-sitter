# target to generate grammar
add_custom_target(generate-grammar)

# Command to generate the grammar
add_custom_command(TARGET generate-grammar PRE_BUILD COMMAND ${CMAKE_COMMAND} -P gen.grammar.cmake)