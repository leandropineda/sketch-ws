"""pcap parser."""
import dpkt
import requests
import socket
import json
import os
from datetime import datetime
import sched
import time

dataset = 'smallFlows.pcap'


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
        event = {
            "event": flow
        }
        evt = (ts - initial_ts, json.dumps(event))
        events.append(evt)

        if not (c % 1000):
            print "Processing packet {}.".format(c)
        c += 1

print "{} were processed.\n".format(c)
print "Scheduled tasks should take: {} seconds".format(ts - initial_ts)


from concurrent.futures import ThreadPoolExecutor
from requests_futures.sessions import FuturesSession

session = FuturesSession(executor=ThreadPoolExecutor(max_workers=16))


def post_event(evt):
    """Post an event."""

    requests.post('http://localhost:8080/event',
                  data=evt,
                  headers={'content-type': 'application/json'})


print "Running  tasks."
t = time.time()
for evt in events:

    while time.time() - t < evt[0]:
        continue
    post_event(evt[1])

t = time.time() - t
print "Took {} seconds to run".format(t)
