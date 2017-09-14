"""pcap parser."""
import dpkt
import requests
import socket
import json
import os

filename = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                        'smallFlows.pcap')

print "Using file " + filename
headers = {
    'content-type': 'application/json'
    }

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

        requests.post('http://localhost:8080/event',
                      data=json.dumps(event),
                      headers=headers)
