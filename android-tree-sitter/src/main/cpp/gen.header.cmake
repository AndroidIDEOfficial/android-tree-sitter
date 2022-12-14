# Generate header files for native methods
execute_process (
        COMMAND bash "${CMAKE_SOURCE_DIR}/utils/genh.sh"
        OUTPUT_VARIABLE outVar
)