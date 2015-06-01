#!/bin/bash

DELIMITER='|'

CLOUD_CONF=./cloud.conf
GCLOUD_KEY=~/.ssh/google_compute_engine

while read LINE;
do
	export VM_NAME=`echo $LINE | cut -d ${DELIMITER} -f 1`
	export VM_DIR=`echo $LINE | cut -d ${DELIMITER} -f 2`
	export AGENTS=`echo $LINE | cut -d ${DELIMITER} -f 3`
	export JAR_FILE=./dispatch-rider-1.0.0-standalone.jar
	export LIB_DIR=./lib/

	if [[ $AGENTS == *"SimulationAgent"* ]];
	then
		MAIN_CONTAINER=${VM_NAME}
		ssh -i ${GCLOUD_KEY} ${VM_NAME} 'bash -s' -- < ./run-main.sh $VM_DIR $AGENTS $JAR_FILE $LIB_DIR
	else
		ssh -i ${GCLOUD_KEY} ${VM_NAME} 'bash -s' -- < ./run-slave.sh $VM_DIR $AGENTS $JAR_FILE $LIB_DIR $MAIN_CONTAINER
	fi
done < ${CLOUD_CONF}

AGENTS="GUIAgent:dtp.jade.gui.GUIAgent"
JAR_FILE=./build/libs/dispatch-rider-1.0.0-standalone.jar
LIB_DIR=./lib

export R_HOME=/usr/lib/R

if [[ $AGENTS == *"GUIAgent"* ]];
then
	OPTS=-Xmx6144m
	PARAMS="-local-host arto.no-ip.info"
else
	OPTS=-Xmx2048m
fi

java $OPTS -Djava.library.path=${LIB_DIR} -jar ${JAR_FILE} -container $PARAMS -host ${MAIN_CONTAINER} -port 1099 -agents ${AGENTS}

ssh -i ${GCLOUD_KEY} ${MAIN_CONTAINER} 'bash -s' -- < ./kill-platform.sh