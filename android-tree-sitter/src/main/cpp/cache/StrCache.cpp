//
// Created by itsaky on 12/16/22.
//

#include "StrCache.h"

UTF16String *StrCache::create(JNIEnv *env, jstring str) {
    std::lock_guard<mutex> guard(lock);
    auto ustr = UTF16String();
    ustr.append(env, str);
    strings.emplace_back(ustr);
    auto result = &strings.back();
    return result;
}

void StrCache::erase(UTF16String *str) {
    std::lock_guard<mutex> guard(lock);
    strings.remove(*str);
}
