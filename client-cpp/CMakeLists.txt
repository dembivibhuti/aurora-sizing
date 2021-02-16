cmake_minimum_required(VERSION 3.17)
project(PerfClient)
#set(CMAKE_BUILD_TYPE Release)
set(CMAKE_CXX_STANDARD 17)
SET(Boost_USE_STATIC_LIBS OFF)
SET(Boost_USE_MULTITHREAD ON)
FIND_PACKAGE(Boost 1.71.0 REQUIRED COMPONENTS system thread regex)
IF (Boost_FOUND)
    INCLUDE_DIRECTORIES(${Boost_INCLUDE_DIRS})
    LINK_DIRECTORIES(${Boost_LIBRARY_DIRS})
ENDIF (Boost_FOUND)

FIND_PACKAGE(prometheus-cpp REQUIRED)

# Locate GTest
#find_package(GTest REQUIRED)
#include_directories(${GTEST_INCLUDE_DIRS})

SET(USED_LIBS ${Boost_SYSTEM_LIBRARY} ${Boost_THREAD_LIBRARY} ${Boost_REGEX_LIBRARY} pthread prometheus-cpp::core prometheus-cpp::pull )

add_executable(PerfClient main.cpp client/client.h client/messages/lookup.h metrics.h)
TARGET_LINK_LIBRARIES(PerfClient ${USED_LIBS})