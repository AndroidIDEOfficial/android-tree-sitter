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

#include <jni.h>
#include <vector>

#include "UTF16String.h"
#include "../cache/StrCache.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16StringFactory_00024Native_newString(JNIEnv *env,
                                                                                      jclass clazz,
                                                                                      jstring source) {
    return (jlong) StrCache::getInstance().create(env, source);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16StringFactory_00024Native_newStringBytes(
        JNIEnv *env, jclass clazz, jbyteArray bytes, jint off, jint len) {
    auto ba = env->GetByteArrayElements(bytes, nullptr);
    auto vec = std::vector<jbyte>();
    for (int i = off; i < off + len; ++i) {
        vec.emplace_back(*(ba + off));
    }
    jlong result = (jlong) StrCache::getInstance().create(vec);
    env->ReleaseByteArrayElements(bytes, ba,JNI_ABORT);
    return result;
}