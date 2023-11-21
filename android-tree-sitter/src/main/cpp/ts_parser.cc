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
#include <mutex>
#include <iostream>

#include "utf16str/UTF16String.h"
#include "utils/ts_obj_utils.h"
#include "utils/ts_exceptions.h"
#include "utils/ts_preconditions.h"
#include "ts__log.h"

#include "ts_parser.h"

/**
 * `TSParserInternal` stores the actual tree sitter parser instance along
 * with the cancellation flag and the cancellation flag mutex.
 */
class TSParserInternal {
 public:

  TSParserInternal() {
    cancellation_flag_mutex = new std::mutex();
    cancellation_flag = new std::atomic<size_t *>(nullptr);
    parser = ts_parser_new();
  }

  ~TSParserInternal() {
    delete cancellation_flag_mutex;
    delete cancellation_flag;
    ts_parser_delete(parser);

    cancellation_flag_mutex = nullptr;
    cancellation_flag = nullptr;
    parser = nullptr;
  }

  TSParser *getParser(JNIEnv *env) {
    if (check_destroyed(env)) {
      return nullptr;
    }

    return this->parser;
  }

  bool begin_round(JNIEnv *env) {
    auto flag = get_cancellation_flag(env);

    if (flag) {
      throw_illegal_state(env,
                          "Parser is already parsing another syntax tree! You must cancel the current parse first!");
      return false;
    }

    // allocate a new cancellation flag
    flag = (size_t *) malloc(sizeof(int));
    set_cancellation_flag(env, flag);

    // set the cancellation flag to '0' to indicate that the parser should continue parsing
    *flag = 0;
    ts_parser_set_cancellation_flag(getParser(env), flag);

    return true;
  }

  void end_round(JNIEnv *env) {

    size_t *flag = get_cancellation_flag(env);

    // release the cancellation flag
    free((size_t *) flag);
    set_cancellation_flag(env, nullptr);
    ts_parser_set_cancellation_flag(getParser(env), nullptr);
  }

  size_t *get_cancellation_flag(JNIEnv *env) {
    if (check_destroyed(env)) {
      return nullptr;
    }

    std::lock_guard<std::mutex> get_lock(*cancellation_flag_mutex);
    return cancellation_flag->load();
  }

  void set_cancellation_flag(JNIEnv *env, size_t *flag) {
    if (check_destroyed(env)) {
      return;
    }

    std::lock_guard<std::mutex> set_lock(*cancellation_flag_mutex);
    cancellation_flag->store(flag);
  }

 private:
  std::mutex *cancellation_flag_mutex;
  std::atomic<size_t *> *cancellation_flag;

  TSParser *parser;

  bool check_destroyed(JNIEnv *env) {
    if (cancellation_flag_mutex == nullptr || cancellation_flag == nullptr
        || parser == nullptr) {
      throw_illegal_state(env, "TSParserInternal has already been destroyed");
      return true;
    }

    return false;
  }
};

static jlong
TSParser_newParser(JNIEnv *env,
                   jclass self) {
  auto parser = new TSParserInternal;
  return (jlong) parser;
}

static void
TSParser_delete(JNIEnv *env,
                jclass self,
                jlong parser_ptr) {
  req_nnp(env, parser_ptr);

  auto parser = (TSParserInternal *) parser_ptr;
  delete parser;
}

static void
TSParser_setLanguage(JNIEnv *env,
                     jclass self,
                     jlong parser,
                     jlong language) {
  req_nnp(env, parser, "parser");
  req_nnp(env, language, "language");
  ts_parser_set_language(((TSParserInternal *) parser)->getParser(env),
                         (TSLanguage *) language);
}

static jlong
TSParser_getLanguage(JNIEnv *env,
                     jclass self,
                     jlong parser) {
  req_nnp(env, parser);
  return (jlong) ts_parser_language(((TSParserInternal *) parser)->getParser(env));
}

static void
TSParser_reset(JNIEnv *env,
               jclass self,
               jlong parser) {
  req_nnp(env, parser);
  TSParser *pParser = ((TSParserInternal *) (parser))->getParser(env);
  LOGD("ts_parser.cc", "Reset parser: %p, language: %p", pParser,
       ts_parser_language(pParser));
  ts_parser_reset(pParser);
}

static void
TSParser_setTimeout(JNIEnv *env,
                    jclass self,
                    jlong parser,
                    jlong macros) {
  req_nnp(env, parser);
  ts_parser_set_timeout_micros(((TSParserInternal *) parser)->getParser(env),
                               macros);
}

static jlong
TSParser_getTimeout(JNIEnv *env,
                    jclass self,
                    jlong parser) {
  req_nnp(env, parser);
  return (jlong) ts_parser_timeout_micros(((TSParserInternal *) parser)->getParser(
      env));
}

static jboolean
TSParser_setIncludedRanges(
    JNIEnv *env,
    jclass self,
    jlong parser,
    jobjectArray ranges) {
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
  return (jboolean) ts_parser_set_included_ranges(((TSParserInternal *) parser)->getParser(
      env), r, count);
}

static jobjectArray
TSParser_getIncludedRanges(
    JNIEnv *env,
    jclass self,
    jlong parser) {
  req_nnp(env, parser);
  jint count;
  const TSRange *ranges =
      ts_parser_included_ranges(((TSParserInternal *) parser)->getParser(env),
                                reinterpret_cast<uint32_t *>(&count));
  jobjectArray result = createRangeArr(env, count);
  req_nnp(env, result, "TSRange[] from factory");

  for (uint32_t i = 0; i < count; i++) {
    const TSRange *r = (ranges + i);
    env->SetObjectArrayElement(result, (jint) i, _marshalRange(env, *r));
  }
  return result;
}

static jlong TSParser_parse(JNIEnv *env,
                            jclass clazz,
                            jlong parser,
                            jlong tree_pointer,
                            jlong str_pointer) {
  req_nnp(env, parser);
  req_nnp(env, str_pointer, "string");
  auto *ts_parser_internal = (TSParserInternal *) parser;
  TSParser *ts_parser = ts_parser_internal->getParser(env);
  TSTree *old_tree = tree_pointer == 0 ? nullptr : (TSTree *) tree_pointer;
  auto *source = as_str(env, str_pointer);

  if (!ts_parser_internal->begin_round(env)) {
    return 0;
  }

  auto src_cstring = source->to_cstring();

  // start parsing
  // if the user cancels the parse while this method is being executed
  // then this will return nullptr
  auto tree = ts_parser_parse_string_encoding(ts_parser,
                                              old_tree,
                                              src_cstring,
                                              source->byte_length(),
                                              TSInputEncodingUTF16);

  ts_parser_internal->end_round(env);
  delete[] src_cstring;


  return (jlong) tree;
}

static jboolean
TSParser_requestCancellation(
    JNIEnv *env,
    jclass clazz,
    jlong parser) {

  auto *parserInternal = (TSParserInternal *) parser;
  auto flag = parserInternal->get_cancellation_flag(env);

  // no parse is in progress
  if (flag == nullptr) {
    LOGD("TSParser",
         "Cannot cancel parsing, no parse is in progress (cancellation flag is nullptr).");
    return false;
  }

  // set the cancellation flag to a non-zero value to indicate that the parse
  // operation has been cancelled
  *flag = 1;
  LOGD("TSParser", "Cancellation flag has been set");
  return true;
}

void TSParser_Native__SetJniMethods(JNINativeMethod *methods, int count) {
  SET_JNI_METHOD(methods, TSParser_Native_newParser, TSParser_newParser);
  SET_JNI_METHOD(methods, TSParser_Native_delete, TSParser_delete);
  SET_JNI_METHOD(methods, TSParser_Native_setLanguage, TSParser_setLanguage);
  SET_JNI_METHOD(methods, TSParser_Native_getLanguage, TSParser_getLanguage);
  SET_JNI_METHOD(methods, TSParser_Native_reset, TSParser_reset);
  SET_JNI_METHOD(methods, TSParser_Native_setTimeout, TSParser_setTimeout);
  SET_JNI_METHOD(methods, TSParser_Native_getTimeout, TSParser_getTimeout);
  SET_JNI_METHOD(methods, TSParser_Native_setIncludedRanges, TSParser_setIncludedRanges);
  SET_JNI_METHOD(methods, TSParser_Native_getIncludedRanges, TSParser_getIncludedRanges);
  SET_JNI_METHOD(methods, TSParser_Native_parse, TSParser_parse);
  SET_JNI_METHOD(methods, TSParser_Native_requestCancellation,
                 TSParser_requestCancellation);
}