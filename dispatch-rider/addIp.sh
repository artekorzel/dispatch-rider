#!/bin/bash

sudo iptables -t nat -F
sudo iptables -t nat -A OUTPUT -d 10.240.172.246 -j DNAT --to-destination 104.155.85.145
sudo iptables -t nat -A OUTPUT -d 10.240.112.47 -j DNAT --to-destination 23.251.134.137
sudo iptables -t nat -A OUTPUT -d 10.240.66.67 -j DNAT --to-destination 104.155.62.11
sudo iptables -t nat -A OUTPUT -d 10.240.91.176 -j DNAT --to-destination 104.155.206.115
