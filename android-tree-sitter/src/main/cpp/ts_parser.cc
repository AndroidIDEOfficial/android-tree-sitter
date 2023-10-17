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

#include <atomic>
#include <iostream>

#include "utf16str/UTF16String.h"
#include "utils/ts_obj_utils.h"
#include "utils/ts_exceptions.h"
#include "utils/ts_preconditions.h"
#include "utils/ts_log.h"

static std::atomic<size_t *> cancellation_flag(nullptr);

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_newParser(
    JNIEnv *env, jclass self) {
  return (jlong) ts_parser_new();
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_delete(
    JNIEnv *env, jclass self, jlong parser) {
  req_nnp(env, parser);
  ts_parser_delete((TSParser *) parser);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setLanguage(
    JNIEnv *env, jclass self, jlong parser, jlong language) {
  req_nnp(env, parser, "parser");
  req_nnp(env, language, "language");
  ts_parser_set_language((TSParser *) parser, (TSLanguage *) language);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getLanguage(
    JNIEnv *env, jclass self, jlong parser) {
  req_nnp(env, parser);
  return (jlong) ts_parser_language((TSParser *) parser);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_reset(JNIEnv *env,
                                                                 jclass self,
                                                                 jlong parser) {
  req_nnp(env, parser);
  ts_parser_reset((TSParser *) parser);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setTimeout(
    JNIEnv *env, jclass self, jlong parser, jlong macros) {
  req_nnp(env, parser);
  ts_parser_set_timeout_micros((TSParser *) parser, macros);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getTimeout(
    JNIEnv *env, jclass self, jlong parser) {
  req_nnp(env, parser);
  return (jlong) ts_parser_timeout_micros((TSParser *) parser);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setIncludedRanges(
    JNIEnv *env, jclass self, jlong parser, jobjectArray ranges) {
  req_nnp(env, parser);
  int count = env->GetArrayLength(ranges);
  TSRange tsRanges[count];
  for (int i = 0; i < count; i++) {
    jobject range = env->GetObjectArrayElement(ranges, i);
    std::string msg = std::string("ranges[") + std::to_string(i) + "]";
    req_nnp(env, range, msg);
    tsRanges[i] = _unmarshalRange(env, range);
  }

  const TSRange *r = tsRanges;
  return (jboolean) ts_parser_set_included_ranges((TSParser *) parser,
                                                  r,
                                                  count);
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getIncludedRanges(
    JNIEnv *env, jclass self, jlong parser) {
  req_nnp(env, parser);
  jint count;
  const TSRange *ranges = ts_parser_included_ranges((TSParser *) parser,
                                                    reinterpret_cast<uint32_t *>(&count));
  jobjectArray result = createRangeArr(env, count);
  req_nnp(env, result, "TSRange[] from factory");

  for (uint32_t i = 0; i < count; i++) {
    const TSRange *r = (ranges + i);
    env->SetObjectArrayElement(result, (jint) i, _marshalRange(env, *r));
  }
  return result;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_parse(JNIEnv *env,
                                                                 jclass clazz,
                                                                 jlong parser,
                                                                 jlong tree_pointer,
                                                                 jlong str_pointer) {
  req_nnp(env, parser);
  req_nnp(env, str_pointer, "string");
  auto *ts_parser = (TSParser *) parser;
  TSTree *old_tree = tree_pointer == 0 ? nullptr : (TSTree *) tree_pointer;
  auto *source = as_str(str_pointer);

  auto flag = cancellation_flag.load();

#pragma clang diagnostic push
#pragma ide diagnostic ignored "ConstantConditionsOC"
#pragma ide diagnostic ignored "UnreachableCode"

  if (flag) {
    throw_illegal_state(env, "Parser is already parsing another syntax tree! You must cancel the current parse first!");
    return 0;
  }

#pragma clang diagnostic pop

  // allocate a new cancellation flag
  flag = (size_t *) malloc(sizeof(int));
  cancellation_flag.store(flag);

  // set the cancellation flag to '0' to indicate that the parser should continue parsing
  *flag = 0;
  ts_parser_set_cancellation_flag(ts_parser, flag);

  // start parsing
  // if the user cancels the parse while this method is being executed
  // then this will return nullptr
  auto tree =
      ts_parser_parse_string_encoding(ts_parser,
                                      old_tree,
                                      source->to_cstring(),
                                      source->byte_length(),
                                      TSInputEncodingUTF16);

  // release the cancellation flag
  free((size_t*) flag);
  cancellation_flag.store(nullptr);

  return (jlong) tree;
}

// <editor-fold desc="Pragma to disable misleading code warning">
#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnreachableCode"
#pragma ide diagnostic ignored "ConstantConditionsOC"
#pragma ide diagnostic ignored "ConstantFunctionResult"
// </editor-fold>

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_cancelIfParsing(
    JNIEnv *env,
    jclass clazz) {

  auto flag = cancellation_flag.load();

  // no parse is in progress
  if (flag == nullptr) {
    LOGD("TSParser", "Cannot cancel parsing, no parse is in progress (cancellation flag is nullptr).");
    return false;
  }

  // set the cancellation flag to a non-zero value to indicate that the parse
  // operation has been cancelled
  *flag = 1;
  return true;
}

// <editor-fold desc="Pragma to disable misleading code warning">
#pragma clang diagnostic pop
// </editor-fold>