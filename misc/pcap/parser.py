"""pcap parser."""
import dpkt

import socket
import json
import os
from datetime import datetime
import sched
import time
import paho.mqtt.publish as publish

dataset = 'bigFlows.pcap'


def print_str_date(ts, event):
    """Print timestamp in strftime."""
    date = datetime.fromtimestamp(ts).strftime('Y-%m-%d %H:%M:%S.%f%z')
    print date, str(event)


filename = os.path.join(
    os.path.dirname(
        os.path.abspath(__file__)),
    dataset)
print "Using file " + filename

initial_ts = 0
with open(filename, 'r') as fd:
    for initial_ts, _ in dpkt.pcap.Reader(fd):
        break

events = list()
c = 0
for ts, pkt in dpkt.pcap.Reader(open(filename, 'r')):

    eth = dpkt.ethernet.Ethernet(pkt)
    if eth.type != dpkt.ethernet.ETH_TYPE_IP:
        continue
    ip = eth.data
    if ip.p == dpkt.ip.IP_PROTO_TCP:
        tcp = ip.data
        flow = "{}:{}->{}:{}".format(
            socket.inet_ntoa(ip.src),
            tcp.sport,
            socket.inet_ntoa(ip.dst),
            tcp.dport)

        evt = (ts - initial_ts, flow)
        events.append(evt)

        if not (c % 1000):
            print "Processing packet {}.".format(c)
        c += 1

print "{} were processed.\n".format(c)
with open('bigFlows.txt', 'w') as fd:
    for e in events:
        fd.write("{},{}\n".format(e[0], e[1]))
