# If not building for Android, we need to include header files from JDK
if (NOT ${CMAKE_SYSTEM_NAME} STREQUAL Android)

    find_package(JNI REQUIRED)

    # Path for jni.h
    include_directories(${JAVA_INCLUDE_PATH})

    # Path for jni_md.h
    include_directories(${JAVA_INCLUDE_PATH2})
endif ()

# If not building with Gradle, these variables won't be set
if (NOT DEFINED PROJECT_DIR)
    set(PROJECT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../../")
endif ()
if (NOT DEFINED TS_DIR)
    set(TS_DIR "${PROJECT_DIR}/tree-sitter-lib")
endif ()