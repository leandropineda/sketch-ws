"""pcap parser."""
import dpkt
import requests
import socket
import json
import os
from datetime import datetime
import sched
import time

url = 'http://localhost:8080/event'
headers = {
    'content-type': 'application/json'
    }
dataset = 'smallFlows.pcap'


def print_str_date(ts, event):
    """Print timestamp in strftime."""
    date = datetime.fromtimestamp(ts).strftime('Y-%m-%d %H:%M:%S.%f%z')
    print date, str(event)


def post_event(data, ts, print_event):
    """Post an event."""
    # event = {
    #     "event": data
    # }
    # requests.post(url,
    #               data=json.dumps(event),
    #               headers=headers)
    # if print_event:
    #     print_str_date(ts, data)


filename = os.path.join(
    os.path.dirname(
        os.path.abspath(__file__)),
    dataset)
print "Using file " + filename

print "Initializing event scheduler"
schd = sched.scheduler(time.time, time.sleep)

initial_ts = 0
with open(filename, 'r') as fd:
    for initial_ts, _ in dpkt.pcap.Reader(fd):
        break

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
        log_event = not (c % 1000)
        schd.enter(ts - initial_ts, 1, post_event, [flow, ts, log_event])
        if (log_event):
            print "Processing packet {}.".format(c)
        c += 1

print "{} were processed.\n".format(c)
print "Scheduled tasks will take: {} seconds".format(ts - initial_ts)

print "Running scheduled tasks (logging every 1000 events)"
t = time.time()
schd.run()
t = time.time() - t
print "Scheduler took {} seconds to run".format(t)
