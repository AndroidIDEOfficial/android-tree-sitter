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

project("tree-sitter-json")

# Include common configuration
include(../../../../cmake/common-config.cmake)

# This includes the header file for the parser
include_directories(grammar/src)

# add tree-sitter-java library
add_library(${CMAKE_PROJECT_NAME} SHARED
        grammar/src/parser.c
        tree-sitter-json.cpp)
