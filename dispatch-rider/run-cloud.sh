#!/bin/bash

export R_HOME=/usr/lib/R

DELIMITER='|'

CLOUD_CONF=./cloud.conf
JAR_FILE=./build/libs/dispatch-rider-1.0.0-standalone.jar
LIB_DIR=./lib/

nohup java -jar ${JAR_FILE} -Xmx2048m -Djava.library.path=${LIB_DIR} -gui "GUIAgent:dtp.jade.gui.GUIAgent" > out.log &

while read LINE;
do
	export VM_NAME=`echo $LINE | cut -d ${DELIMITER} -f 1`
	export VM_DIR=`echo $LINE | cut -d ${DELIMITER} -f 2`
	export AGENTS=`echo $LINE | cut -d ${DELIMITER} -f 3`

	ssh ${VM_NAME} 'bash -s' -- < ./run_slave.sh $VM_NAME $VM_DIR $AGENTS
done < ${CLOUD_CONF}