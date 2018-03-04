"""Generate events."""
import requests
import json
import random
import time
import string
import collections
import os
import random
import paho.mqtt.client as mqtt


def generate_events(n_events, universe_size):
    """
    Generate n_events events from a universe of the given size.
    """
    params = [n_events, universe_size]
    assert(all([type(x) == int for x in params]))

    return [str(random.randint(0, universe_size)) for _ in range(n_events)]

if __name__ == '__main__':
    # Config variables
    results_file_path = ''
    n_events = 100000000
    universe_size = 10000

    noise_events = generate_events(n_events, universe_size)
    print("Connecting...")
    client = mqtt.Client()
    client.connect('mosquitto')
    print "Connected"

    print "Publishing events"
    for i in range(n_events):
        client.publish("events", noise_events[i])



