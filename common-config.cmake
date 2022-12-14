# If not building for Android, we need to include header files from JDK
set(IS_ANDROID_BUILD TRUE)
if (NOT ${CMAKE_SYSTEM_NAME} STREQUAL Android)
    set(IS_ANDROID_BUILD FALSE)

    find_package(JNI REQUIRED)

    # Path for jni.h
    include_directories(${JAVA_INCLUDE_PATH})

    # Path for jni_md.h
    include_directories(${JAVA_INCLUDE_PATH2})
endif ()

# If not building with Gradle, these variables won't be set
if (NOT DEFINED PROJECT_DIR)
    if (NOT ${IS_ANDROID_BUILD})
        message(WARNING "Building with Gradle is recommended")
    endif ()
    set(PROJECT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../../")
endif ()
if (NOT DEFINED TS_DIR)
    set(TS_DIR "${PROJECT_DIR}/tree-sitter-lib")
endif ()