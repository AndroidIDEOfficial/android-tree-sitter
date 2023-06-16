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

#ifndef ATS_TS_LOG_H
#define ATS_TS_LOG_H

#define LOG_TAG "android-tree-sitter"

#ifdef ANDROID
#include <android/log.h>

// For android

#define LOGE(TAG, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGW(TAG, ...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGD(TAG, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGI(TAG, ...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGV(TAG, ...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)

#else
#include <cstdio>

// for other systems
// required for tests

#define LOGE(TAG, ...) printf("[%s] ERROR: ", TAG); printf(__VA_ARGS__); printf("\n")
#define LOGW(TAG, ...) printf("[%s] WARNING: ", TAG); printf(__VA_ARGS__); printf("\n")
#define LOGD(TAG, ...) printf("[%s] DEBUG: ", TAG); printf(__VA_ARGS__); printf("\n")
#define LOGI(TAG, ...) printf("[%s] INFO: ", TAG); printf(__VA_ARGS__); printf("\n")
#define LOGV(TAG, ...) printf("[%s] VERBOSE: ", TAG); printf(__VA_ARGS__); printf("\n")

#endif // ANDROID

#endif //ATS_TS_LOG_H
