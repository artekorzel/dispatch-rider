#!/bin/bash

kill `ps aux | grep java | grep -v grep | grep SimulationAgent | awk '{print $2}'`