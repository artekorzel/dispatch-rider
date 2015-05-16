#!/bin/bash

R_HOME=/usr/lib/R java -jar ./build/libs/dispatch-rider-1.0.0-standalone.jar -Xmx2048m -Djava.library.path=./lib/ -container -host arto.no-ip.info -port 1099 -agents "DistributorAgent:dtp.jade.distributor.DistributorAgent;CrisisManagerAgent:dtp.jade.crisismanager.CrisisManagerAgent;InfoAgent:dtp.jade.info.InfoAgent;SimulationAgent:dtp.jade.simulation.SimulationAgent"
