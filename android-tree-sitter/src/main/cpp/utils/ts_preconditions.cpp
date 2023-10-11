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

#include "ts_preconditions.h"
#include "ts_exceptions.h"
#include "ts_log.h"

#include <string>

void req_nnp(JNIEnv *env, jlong ref, const std::string& name) {
  req_nnp(env, (void *) ref, name);
}

void req_nnp(JNIEnv *env, void *p, const std::string& name) {
  if (p == nullptr) {
    std::string msg = name + " == nullptr";
    throw_illegal_args(env, msg.c_str());
  }
}

void req_nnp(JNIEnv *env, jobject& obj, std::string& objName) {
  if (obj == nullptr) {
    objName += " == nullptr";
    throw_illegal_args(env, objName.c_str());
  }
}

void req_nnp(JNIEnv *env, jobject& obj, const char* objName) {
  std::string name(objName);
  req_nnp(env, obj, name);
}
