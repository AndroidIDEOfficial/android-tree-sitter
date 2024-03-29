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
#include "tree_sitter/api.h"

struct TreeCursorNode {
  const char *type;
  const char *name;
  uint32_t startByte;
  uint32_t endByte;
};

#define _loadClass(VARIABLE, NAME)             \
  {                                            \
    jclass tmp;                                \
    tmp = env->FindClass(NAME);                \
    VARIABLE = (jclass)env->NewGlobalRef(tmp); \
    env->DeleteLocalRef(tmp);                  \
  }

#define _loadField(VARIABLE, CLASS, NAME, TYPE) \
  { VARIABLE = env->GetFieldID(CLASS, NAME, TYPE); }


#define _loadStaticMethod(VARIABLE, CLASS, NAME, SIGNATURE) \
  {                                                   \
    VARIABLE = env->GetStaticMethodID(CLASS, NAME, SIGNATURE);                                                  \
  }
void onLoad(JNIEnv *env);

void onUnload(JNIEnv *env);

jobject _marshalNode(JNIEnv *env, TSNode node);
TSNode _unmarshalNode(JNIEnv *env, jobject javaObject);

jobject _marshalPoint(JNIEnv *env, TSPoint point);
TSPoint _unmarshalPoint(JNIEnv *env, jobject javaObject);

jobject _marshalRange(JNIEnv *env, TSRange range);
TSRange _unmarshalRange(JNIEnv *env, jobject javaObject);
jobjectArray createRangeArr(JNIEnv *env, jint size);

jobject _marshalMatch(JNIEnv *env, TSQueryMatch match);
jobject _marshalCapture(JNIEnv *env, TSQueryCapture capture);

jobject _marshalTreeCursorNode(JNIEnv *env, TreeCursorNode node);

TSInputEdit _unmarshalInputEdit(JNIEnv *env, jobject inputEdit);

jobject
_marshalQueryPredicateStep(JNIEnv *env, const TSQueryPredicateStep *predicate);
jobjectArray createQueryPredicateStepArr(JNIEnv *env, jint size);