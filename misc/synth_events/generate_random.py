"""Generate events."""
import requests
import json
import random
import time
import string
import collections
import os
import random


url = 'http://localhost:8080/event'
headers = {
    'content-type': 'application/json'
}

def post_event(data):
    """Post an event."""
    event = {"event": data}
    requests.post(url, data=json.dumps(event), headers=headers)

def generate_events(n_events, universe_size):
    """
    Generate n_events events from a universe of the given size.
    """
    params = [n_events, universe_size]
    assert(all([type(x) == int for x in params]))

    raw_events = list()
    for _ in range(n_events):
        random_n = random.randint(0, universe_size)
        raw_events.append(str(random_n))
    return raw_events

if __name__ == '__main__':
    # Config variables
    results_file_path = ''
    n_events = 1000000
    universe_size = 10000
    
    noise_events = generate_events(n_events, universe_size)
    print "Posting events"
    for i in range(n_events):
        post_event(noise_events[i])



