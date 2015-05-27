#!/bin/bash

sudo iptables -t nat -F
sudo iptables -t nat -A OUTPUT -d 10.240.67.142 -j DNAT --to-destination 146.148.116.67
sudo iptables -t nat -A OUTPUT -d 10.240.36.229 -j DNAT --to-destination 104.155.45.157
sudo iptables -t nat -A OUTPUT -d 10.240.235.27 -j DNAT --to-destination 104.155.51.42
sudo iptables -t nat -A OUTPUT -d 10.240.137.81 -j DNAT --to-destination 104.155.44.112
