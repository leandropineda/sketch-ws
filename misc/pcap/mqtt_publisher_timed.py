import time
import paho.mqtt.client as mqtt

evts = list()

# parse txt file by using parser.py
file_path = '/Users/lpineda/workspace/sketch-ws/misc/pcap/bigFlows.txt'
with open(file_path, 'r') as fd:
    for e in fd:
        t = float(e.split(',')[0])
        evt = e.split(',')[1].rstrip()
        evts.append((t, evt))

print "Parsed {} events".format(len(evts))
print evts[:10]

start_time = time.time()
client = mqtt.Client()
client.connect('127.0.0.1')
i = 0
t0 = time.time()
for e in evts:
    while time.time() - t0 < e[0]:
        pass
    client.publish("events", e[1])
    i += 1
    if (i % 10000 == 0):
        print i, e

print("--- %s seconds ---" % (time.time() - start_time))
