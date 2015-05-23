#!/bin/bash

VM_NAME=$1
VM_DIR=$2
AGENTS=$3

echo $VM_NAME
echo $VM_DIR
echo $AGENTS

HOST=arto.no-ip.info
PORT=1099

JAR_FILE=./dispatch-rider-1.0.0-standalone.jar
LIB_DIR=./lib/

export R_HOME=/usr/lib/R

cd ${VM_DIR}

nohup java -Xmx2048m -Djava.library.path=${LIB_DIR} -jar ${JAR_FILE} -container -host ${HOST} -port ${PORT} -agents ${AGENTS} > out.log &