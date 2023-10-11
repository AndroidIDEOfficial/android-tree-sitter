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

#include "ts_exceptions.h"
#include "ts_log.h"

#define TS_EXCEPTIONS "TreeSitter_Exceptions"

int throw_exception(JNIEnv *env, const char *klass, const char *message) {
  jclass exception = env->FindClass(klass);
  if (exception == nullptr) {
    LOGE(TS_EXCEPTIONS, "Unable to find exception class: %s", klass);
    return -1;
  }

  return env->ThrowNew(exception, message);
}

int throw_illegal_args(JNIEnv *env, const char *message) {
  return throw_exception(env, "java/lang/IllegalArgumentException", message);
}
