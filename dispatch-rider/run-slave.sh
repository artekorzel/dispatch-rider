#!/bin/bash

VM_DIR=$1
AGENTS=$2
JAR_FILE=$3
LIB_DIR=$4
HOST=$5
PORT=1099

export R_HOME=/usr/lib/R

cd ${VM_DIR}

nohup java -Xmx2048m -Djava.library.path=${LIB_DIR} -jar ${JAR_FILE} -container -host ${HOST} -port ${PORT} -agents ${AGENTS} > out.log &