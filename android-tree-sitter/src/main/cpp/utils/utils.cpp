/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/\>.
 */

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