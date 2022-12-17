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

#include "StrCache.h"

#include <utility>

UTF16String *StrCache::create(JNIEnv *env, jstring str) {
    auto ustr = UTF16String();
    ustr.append(env, str);
    return register_str(ustr);
}

UTF16String *StrCache::create(vector<jbyte> bytes) {
    return register_str(UTF16String(std::move(bytes)));
}

UTF16String *StrCache::register_str(const UTF16String &ustr) {
    std::lock_guard<mutex> guard(lock);
    strings.emplace_back(ustr);
    auto result = &strings.back();
    return result;
}

void StrCache::erase(UTF16String *str) {
    std::lock_guard<mutex> guard(lock);
    strings.remove(*str);
}