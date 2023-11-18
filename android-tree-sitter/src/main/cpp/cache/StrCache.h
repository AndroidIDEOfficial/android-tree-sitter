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

#ifndef ANDROIDTREESITTER_STRCACHE_H
#define ANDROIDTREESITTER_STRCACHE_H

#include <jni.h>
#include <list>
#include <vector>
#include <mutex>

#include "../utf16str/UTF16String.h"

class StrCache {
 private:
  list<UTF16String> strings;
  mutex lock;
 public:
  UTF16String *create(JNIEnv *env, jstring str);
  UTF16String *create(vector<jbyte> bytes);
  UTF16String *register_str(const UTF16String &str);
  void erase(UTF16String *str);

  static StrCache &getInstance() {
    static StrCache instance;
    return instance;
  }
};

#endif //ANDROIDTREESITTER_STRCACHE_H