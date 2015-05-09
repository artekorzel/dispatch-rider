#!/bin/bash

R_HOME=/usr/lib/R java -jar ./build/libs/dispatch-rider-1.0.0-standalone.jar -Xmx2048m -Djava.library.path=./lib/ -gui "GUIAgent:dtp.jade.gui.GUIAgent;DistributorAgent:dtp.jade.distributor.DistributorAgent;CrisisManagerAgent:dtp.jade.crisismanager.CrisisManagerAgent;InfoAgent:dtp.jade.info.InfoAgent"
