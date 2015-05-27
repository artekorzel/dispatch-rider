#!/bin/bash

export R_HOME=/usr/lib/R

DELIMITER='|'

CLOUD_CONF=./cloud.conf
JAR_FILE=./build/libs/dispatch-rider-1.0.0-standalone.jar
LIB_DIR=./lib
GCLOUD_KEY=~/.ssh/google_compute_engine

nohup java -Xmx2048m -Djava.library.path=${LIB_DIR}/ -jar ${JAR_FILE} -local-host arto.no-ip.info -gui "GUIAgent:dtp.jade.gui.GUIAgent" > out.log &

while read LINE;
do
	export VM_NAME=`echo $LINE | cut -d ${DELIMITER} -f 1`
	export VM_DIR=`echo $LINE | cut -d ${DELIMITER} -f 2`
	export AGENTS=`echo $LINE | cut -d ${DELIMITER} -f 3`

	ssh -i ${GCLOUD_KEY} ${VM_NAME} 'bash -s' -- < ./run-slave.sh $VM_NAME $VM_DIR $AGENTS
done < ${CLOUD_CONF}
