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

#include <iostream>

#include "utf16str/UTF16String.h"
#include "utils/ts_obj_utils.h"

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_newParser(
        JNIEnv *env, jclass self) {
    return (jlong) ts_parser_new();
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_delete(
        JNIEnv *env, jclass self, jlong parser) {
    ts_parser_delete((TSParser *) parser);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setLanguage(
        JNIEnv *env, jclass self, jlong parser, jlong language) {
    ts_parser_set_language((TSParser *) parser, (TSLanguage *) language);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getLanguage(
        JNIEnv *env, jclass self, jlong parser) {
    return (jlong) ts_parser_language((TSParser *) parser);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_reset(JNIEnv *env,
                                                                 jclass self,
                                                                 jlong parser) {
    ts_parser_reset((TSParser *) parser);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setTimeout(
        JNIEnv *env, jclass self, jlong parser, jlong macros) {
    ts_parser_set_timeout_micros((TSParser *) parser, macros);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getTimeout(
        JNIEnv *env, jclass self, jlong parser) {
    return (jlong) ts_parser_timeout_micros((TSParser *) parser);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setIncludedRanges(
        JNIEnv *env, jclass self, jlong parser, jobjectArray ranges) {
    int count = env->GetArrayLength(ranges);
    TSRange tsRanges[count];
    for (int i = 0; i < count; i++) {
        tsRanges[i] = _unmarshalRange(env, env->GetObjectArrayElement(ranges, i));
    }

    const TSRange *r = tsRanges;
    return (jboolean) ts_parser_set_included_ranges((TSParser *) parser, r, count);
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getIncludedRanges(
        JNIEnv *env, jclass self, jlong parser) {
    jint count;
    const TSRange *ranges = ts_parser_included_ranges((TSParser *) parser,
                                                      reinterpret_cast<uint32_t *>(&count));
    jclass klass = env->FindClass("com/itsaky/androidide/treesitter/TSRange");
    jobjectArray result = env->NewObjectArray(count, klass, nullptr);
    for (uint32_t i = 0; i < count; i++) {
        const TSRange *r = (ranges + i);
        env->SetObjectArrayElement(result, i, _marshalRange(env, *r));
    }
    return result;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_parse(JNIEnv *env, jclass clazz,
                                                                 jlong parser,
                                                                 jlong tree_pointer,
                                                                 jlong str_pointer) {
    auto *ts_parser = (TSParser *) parser;
    TSTree *old_tree = tree_pointer == 0 ? nullptr : (TSTree *) tree_pointer;
    auto *source = as_str(str_pointer);
    auto tree = ts_parser_parse_string_encoding(ts_parser, old_tree, source->to_cstring(),
                                                source->byte_length(), TSInputEncodingUTF16);
    return (jlong) tree;
}