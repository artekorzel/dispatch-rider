#!/bin/bash

R_HOME=/usr/lib/R java -Xmx14g -Djava.library.path=./lib/ -jar ./build/libs/dispatch-rider-1.0.0-standalone.jar -gui "GUIAgent:dtp.jade.gui.GUIAgent;DistributorAgent:dtp.jade.distributor.DistributorAgent;CrisisManagerAgent:dtp.jade.crisismanager.CrisisManagerAgent;InfoAgent:dtp.jade.info.InfoAgent;SimulationAgent:dtp.jade.simulation.SimulationAgent;VMAgent:dtp.jade.vm.VMAgent"
