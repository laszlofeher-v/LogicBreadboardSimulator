cmake_minimum_required(VERSION 3.22.1)

project("crouter-jni" C)

add_library(crouter-jni SHARED
    src/main/jni/crouter-jni.c)

# Ensure 16 KB ELF segment alignment for Android 15+ (API 35+) compatibility
target_link_options(crouter-jni PRIVATE "-Wl,-z,max-page-size=16384")

find_library(log-lib log)

target_link_libraries(crouter-jni
    ${log-lib}
    android)
