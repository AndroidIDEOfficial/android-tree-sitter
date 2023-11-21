# If not building for Android, we need to include header files from JDK
if (NOT ${CMAKE_SYSTEM_NAME} STREQUAL Android)

    find_package(JNI REQUIRED)

    # Path for jni.h
    include_directories(${JAVA_INCLUDE_PATH})

    # Path for jni_md.h
    include_directories(${JAVA_INCLUDE_PATH2})
endif ()

if (NOT DEFINED PROJECT_CMAKE_DIR)
    set(PROJECT_CMAKE_DIR "${PROJECT_DIR}/cmake")
endif ()

if (NOT DEFINED TS_DIR)
    set(TS_DIR "${PROJECT_DIR}/tree-sitter-lib")
endif ()

# Include paths from tree-sitter
set(TS_INCLUDES ${TS_DIR}/lib/include ${TS_DIR}/lib/src)

# tree-sitter header files
include_directories(${TS_INCLUDES})

# Auto-generated headers
include_directories(${AUTOGEN_HEADERS})