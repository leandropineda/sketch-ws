"""apache_log parser."""
import re
import json
from datetime import datetime
import requests
import sched
import time

from generate import post_event
import matplotlib.pyplot as plt


def main():
    """main"""

    with open('/Users/lpineda/workspace/sketch-ws/misc/apache_logs/apache.log',
              'r') as fd_logs:
        log_file = fd_logs.readlines()

    """
    10.20.8.52
    -
    -
    [01/Dec/2016:12:34:14 -0300]
    "POST /actuaciones/ajax/gestionar-localidades.php HTTP/1.1"
    200
    331
    "https://tapp.santafe.gov.ar/actuaciones/entrega.php"
    "Mozilla/5.0 (X11; Linux x86_64; rv:43.0) Gecko/20100101 Firefox/43.0"
    "10.1.15.161"
    """

    parts = [
        r'(?P<host>\S+)',                   # host %h
        r'\S+',                             # indent %l (unused)
        r'(?P<user>\S+)',                   # user %u
        r'\[(?P<time>.+)\]',                # time %t
        r'"(?P<request>.+)"',               # request "%r"
        r'(?P<status>[0-9]+)',              # status %>s
        r'(?P<size>\S+)',                   # size %b (careful, can be '-')
        r'"(?P<referer>.*)"',               # referer "%{Referer}i"
        r'"(?P<agent>.*)"',                 # user agent "%{User-agent}i"
        r'"(?P<ip>\S+)"'                    # IP Address
    ]

    headers = {'content-type': 'application/json'}

    pattern = re.compile(r'\s+'.join(parts)+r'\s*\Z')
    count = 0

    events_time = []
    schd = sched.scheduler(time.time, time.sleep)

    t0 = None

    for line in log_file:
        count += 1
        if count > 2000:
            break
        match = pattern.match(line)
        event = match.groupdict()
        hit = {
            "event": event['request'] if len(event['request'].split('?')) == 1
            else event['request'].split('?')[0] + " HTTP/1.1"
        }
        t = int(datetime.strptime(event['time'].split()[0], '%d/%b/%Y:%H:%M:%S').
                strftime("%s"))
        t0 = t if count == 1 else t0
        t -= t0
        events_time.append(t)
        schd.enter(t, 1, post_event, [hit, t, True])

    plot_histogaram(events_time)

    schd.run()


def plot_histogaram(events):
    print "There are {} events.".format(len(events))

    hist, bin_edges, patches = plt.hist(events, 50)

    bin_edges_datetime = map(
        lambda x: datetime.fromtimestamp(x).strftime('%Y-%m-%d %H:%M:%S'),
        bin_edges)

    plt.bar(bin_edges[:-1], hist, width=1)
    plt.xlim(min(bin_edges), max(bin_edges))
    bin_edges = map(lambda x: int(x), bin_edges)
    plt.xticks(bin_edges, bin_edges, rotation='vertical')
    plt.show()

if __name__ == '__main__':
    main()
