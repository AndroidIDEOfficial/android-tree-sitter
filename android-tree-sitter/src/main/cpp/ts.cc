/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

#include <array>
#include <tree_sitter/api.h>

#include "ts__log.h"
#include "ts_meta.h"

static jint ats_language_version(JNIEnv *env, jclass self) {
  return (jint) TREE_SITTER_LANGUAGE_VERSION;
}

static jint ats_min_compatible_language_version(JNIEnv *env, jclass self) {
  return (jint) TREE_SITTER_MIN_COMPATIBLE_LANGUAGE_VERSION;
}

void TreeSitter_Native__SetJniMethods(JNINativeMethod *methods, int count) {

  SET_JNI_METHOD(methods, TreeSitter_Native_getLanguageVersion, ats_language_version);
  SET_JNI_METHOD(methods, TreeSitter_Native_getMinimumCompatibleLanguageVersion,
                 ats_min_compatible_language_version);
}