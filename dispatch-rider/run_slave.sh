#!/bin/bash

R_HOME=/usr/lib/R java -jar ./build/libs/dispatch-rider-1.0.0-standalone.jar -Xmx2048m -Djava.library.path=./lib/ -container -host localhost -port 1099 -agents InfoAgent:dtp.jade.info.InfoAgent