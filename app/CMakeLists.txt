cmake_minimum_required(VERSION 3.4.1)
add_library( # Sets the name of the library.
             SupperPassword
             SHARED
             src/main/cpp/SupperPassword.cpp)
add_library(wzh033 SHARED IMPORTED)
set_target_properties(wzh033  PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/app/libs/mips/libserialport.so)
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log)
 target_link_libraries( # Specifies the target library.
                       SupperPassword

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib})