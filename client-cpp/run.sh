#!/bin/sh

rm -rf ~/aurora-sizing/client-cpp/CMakeFiles
rm CMakeCache.txt

~/environment/cmake-3.18.0/bin/cmake CMakeLists.txt
~/environment/cmake-3.18.0/bin/cmake  --build .
