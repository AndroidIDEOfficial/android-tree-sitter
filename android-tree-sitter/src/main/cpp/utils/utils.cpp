//
// Created by itsaky on 12/15/22.
//

#include <sstream>

#include "jni_utils.h"
#include "utils.h"

void validate_index(JNIEnv *env, int size, int index) {
    if (index < 0 || index >= size) {
        std::ostringstream stream;
        stream << "size: ";
        stream << size;
        stream << ", index: ";
        stream << index;
        throw_ioob(env, stream.str().c_str());
    }
}
