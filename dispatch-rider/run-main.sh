#!/bin/bash

VM_DIR=$1
AGENTS=$2
JAR_FILE=$3
LIB_DIR=$4

export R_HOME=/usr/lib/R

cd ${VM_DIR}

if [[ $AGENTS == *"GuiAgent"* ]];
then
	OPTS=-Xmx6144m
	PARAMS=-local-host=arto.no-ip.info
else
	OPTS=-Xmx2048m
fi

nohup java $OPTS -Djava.library.path=${LIB_DIR} -jar ${JAR_FILE} -nogui $PARAMS -agents ${AGENTS} > out.log &