"""apache_log parser."""
import re
import requests
import json

with open('/home/leandro/workspace/sketch-ws/misc/apache_logs/apache.log',
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
for line in log_file:
    count += 1
    if count > 2000:
        break
    m = pattern.match(line)
    hit = m.groupdict()
    event = {
        "event": hit['request']
    }
    print event
    requests.post('http://localhost:8080/event',
                  data=json.dumps(event),
                  headers=headers)
