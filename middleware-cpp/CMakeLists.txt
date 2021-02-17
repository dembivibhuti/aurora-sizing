cmake_minimum_required(VERSION 3.17)
project(middleware)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_BUILD_TYPE Release)
SET(Boost_USE_STATIC_LIBS OFF)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O0")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O0")
SET(Boost_USE_MULTITHREAD ON)
#SET(CMAKE_CXX_FLAGS -DBOOST_ASIO_ENABLE_HANDLER_TRACKING)
FIND_PACKAGE(Boost 1.71.0 REQUIRED COMPONENTS system thread regex)
find_package(PostgreSQL REQUIRED)
INCLUDE_DIRECTORIES(${PostgreSQL_INCLUDE_DIRS})
Link_directories(${PostgreSQL_LIBRARY_DIRS})
IF (Boost_FOUND)
    INCLUDE_DIRECTORIES(${Boost_INCLUDE_DIRS})
    LINK_DIRECTORIES(${Boost_LIBRARY_DIRS})
ENDIF (Boost_FOUND)

# Locate GTest
#find_package(GTest REQUIRED)
#include_directories(${GTEST_INCLUDE_DIRS})
include_directories(includes)

find_library(TAO_PQ taopq lib)

FIND_PACKAGE(prometheus-cpp REQUIRED)

SET(USED_LIBS ${Boost_SYSTEM_LIBRARY} ${Boost_THREAD_LIBRARY} ${Boost_REGEX_LIBRARY} pthread ${TAO_PQ} ${PostgreSQL_LIBRARIES} prometheus-cpp::core prometheus-cpp::pull )

ADD_EXECUTABLE(saral main.cpp server/protocol/messages/attach.h server/secserv.h server/io_context_pool.h server/connection.h server/protocol/messages/message.h server/protocol/messages/request.h server/protocol/msg_processor.h server/protocol/messages/getbyname.h server/protocol/messages/security.h server/db/repository.h server/protocol/messages/namelookup.h server/metrics.h)
TARGET_LINK_LIBRARIES(saral ${USED_LIBS})
