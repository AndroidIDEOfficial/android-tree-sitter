//
// Created by itsaky on 12/16/22.
//

#ifndef ANDROIDTREESITTER_STRCACHE_H
#define ANDROIDTREESITTER_STRCACHE_H

#include <jni.h>
#include <list>
#include <mutex>

#include "../utf16str/UTF16String.h"

class StrCache {
private:
    list<UTF16String> strings;
    mutex lock;
public:
    UTF16String *create(JNIEnv *env, jstring str);
    void erase(UTF16String *str);
};


#endif //ANDROIDTREESITTER_STRCACHE_H
