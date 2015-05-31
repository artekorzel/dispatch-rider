#!/bin/bash

DELIMITER='|'

CLOUD_CONF=./cloud.conf
JAR_FILE=./build/libs/dispatch-rider-1.0.0-standalone.jar
LIB_DIR=./lib
GCLOUD_KEY=~/.ssh/google_compute_engine

while read LINE;
do
	export VM_NAME=`echo $LINE | cut -d ${DELIMITER} -f 1`
	export VM_DIR=`echo $LINE | cut -d ${DELIMITER} -f 2`

	#ssh -i ${GCLOUD_KEY} ${VM_NAME} "mkdir -p ${VM_DIR}/lib"
	scp -i ${GCLOUD_KEY} ${JAR_FILE} ${VM_NAME}:${VM_DIR}/
	scp -i ${GCLOUD_KEY} ${LIB_DIR}/* ${VM_NAME}:${VM_DIR}/lib/

	echo "Done $VM_NAME"
done < ${CLOUD_CONF}
