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

def initialize_results_file(file_path):
    with open(file_path, 'w') as fd:
        fd.close()

def save_results_to_file(results, file_path):
    assert os.path.exists(file_path)

    with open(file_path, 'a') as fd:
        fd.write('\n'.join([str(x) for x in results]))


if __name__ == '__main__':
    # Config variables
    results_file_path = ''
    simulation_time = 10
    n_experiments = 50
    n_events_per_experiment = 100000
    universe_size = 10000
    
    results_file_path += "results_n_events_{}_universe_size_{}.txt".format(n_events_per_experiment, universe_size)
    initialize_results_file(results_file_path)
    results = list()
    for e in range(n_experiments):
        noise_events = generate_events(n_events_per_experiment, universe_size)
        t = time.time()
        for i in range(len(noise_events)):
            if time.time() - t > simulation_time:
                print "{}/{} Elapsed {} seconds. Processed {} events.".format(e + 1, n_experiments, time.time() - t, i)
                results.append(i)
                break
            post_event(noise_events[i])
    save_results_to_file(results, results_file_path)



