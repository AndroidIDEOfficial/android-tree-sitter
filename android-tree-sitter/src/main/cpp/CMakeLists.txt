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

cmake_minimum_required(VERSION 3.22.1)

# Project name
project("android-tree-sitter")

set(PROJECT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../..)

# Include common configuration
include(${CMAKE_CURRENT_SOURCE_DIR}/../../../../cmake/common-config.cmake)

# android-tree-sitter uses some internal APIs
# headers for those are located in the tree-sitter sources
include_directories("${TS_DIR}/lib/src")

# Auto-generated headers
include_directories(${AUTOGEN_HEADERS})

# Needed by auto-generated headers
include_directories(${CMAKE_CURRENT_SOURCE_DIR})

# add android-tree-sitter library
add_library(android-tree-sitter SHARED

        # tree-sitter-sources
        "${TS_DIR}/lib/src/lib.c"

        # android-tree-sitter sources
        main.cc
        ts.cc
        ts_cursor.cc
        ts_language.cc
        ts_lookahead_iterator.cc
        ts_node.cc
        ts_parser.cc
        ts_query.cc
        ts_query_cursor.cc
        ts_tree.cc
        cache/StrCache.cpp
        utf16str/JavaUTF16String.cpp
        utf16str/JavaUTF16StringFactory.cpp
        utf16str/UTF16String.cpp
        utils/jni_string.cpp
        utils/ts_exceptions.cpp
        utils/ts_preconditions.cpp
        utils/ts_obj_utils.cpp
        )

if (${CMAKE_SYSTEM_NAME} STREQUAL Android)
    # Find the log library and link it to android-tree-sitter
    find_library(log log)
    target_link_libraries(android-tree-sitter ${log})
endif()
