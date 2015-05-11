#!/bin/bash

LOCAL_HOST=`ifconfig | grep inet | awk '{print $2}' | cut -d ':' -f 2 | grep ^10`

R_HOME=/usr/lib/R java -jar ./build/libs/dispatch-rider-1.0.0-standalone.jar -Xmx2048m -Djava.library.path=./lib/ -container -host arto.no-ip.info -port 1099 -local-host $LOCAL_HOST -agents VMAgent:dtp.jade.vm.VMAgent