#
#  This file is part of android-tree-sitter.
#
#  android-tree-sitter library is free software; you can redistribute it and/or
#  modify it under the terms of the GNU Lesser General Public
#  License as published by the Free Software Foundation; either
#  version 2.1 of the License, or (at your option) any later version.
#
#  android-tree-sitter library is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
#  Lesser General Public License for more details.
#
#   You should have received a copy of the GNU General Public License
#  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
#

## Extracts debugging information from shared libraries
## The following variables must be set before this file is included :
## - TS_LIBRARY_NAME : The name of the shared library in the format (the value that is provided to add_library function).
## - PROJECT_DIR : The project's directory. The debug symbols will be stored in <project-dir>/build/ts_debuginfo/lib<lib-name>-<host-name>-<host-arch>.so.debuginfo

# Explicitly set the output directory for the shared library
set_target_properties(
        ${TS_LIBRARY_NAME}
        PROPERTIES LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/libs"
)

# Specify the debug symbols filename in the following format :
# lib<library-name>-<host-system-name>-<host-system-arch>.so.debuginfo
if (${CMAKE_SYSTEM_NAME} STREQUAL Android)
    set(TS_DEBUG_SYMS_FILE_NAME "lib${TS_LIBRARY_NAME}-Android-${ANDROID_ABI}.so.debuginfo")
else ()
    set(TS_DEBUG_SYMS_FILE_NAME "lib${TS_LIBRARY_NAME}-${CMAKE_HOST_SYSTEM_NAME}-${CMAKE_HOST_SYSTEM_PROCESSOR}.so.debuginfo")
endif ()

set(TS_DEBUG_SYMS_DIR "${PROJECT_DIR}/build/ts_debuginfo")
set(TS_DEBUG_SYMS_FILE_PATH "${TS_DEBUG_SYMS_DIR}/${TS_DEBUG_SYMS_FILE_NAME}")

add_custom_command(
        TARGET ${TS_LIBRARY_NAME}
        POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E make_directory "${TS_DEBUG_SYMS_DIR}"
)
# Export debug symbols
add_custom_command(
        TARGET ${TS_LIBRARY_NAME}
        POST_BUILD
        COMMAND ${CMAKE_OBJCOPY} --only-keep-debug $<TARGET_FILE:${TS_LIBRARY_NAME}> "${TS_DEBUG_SYMS_FILE_PATH}"
)