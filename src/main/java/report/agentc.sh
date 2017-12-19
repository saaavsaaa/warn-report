#!/bin/bash
BASE_HOME=/home/aaa/Code/agent
INCLUDES="-I$JAVA_HOME/include -I$JAVA_HOME/include/linux"
g++ agentest.cpp $INCLUDES -Wall -Wno-deprecated -fPIC --share -o $BASE_HOME/agentest.so
