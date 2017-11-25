"""Generate events."""
import requests
import json
import sched
import random
import time
import string
import matplotlib.pyplot as plt
import collections
import numpy as np
from datetime import datetime
from itertools import groupby
from pandas import DataFrame


def post_event(data, ts):
    """Post an event."""
    url = 'http://localhost:8080/event'
    headers = {
        'content-type': 'application/json'
        }
    event = {
        "event": data
    }

    requests.post(url,
                  data=json.dumps(event),
                  headers=headers)


def group_events_by_time(events, start_time, length, clean_up_interval):
    """Convert vector for plotting."""
    def round_time_to_size(timestamp, size):
        """Return the corresponding bucket for the given timestamp."""
        return int((timestamp - start_time)/size)
    filtered_events = filter(lambda x: x[0] > 0 and x[0] < length, events)
    rounded_timestamp = map(
        lambda x: round_time_to_size(x[0], clean_up_interval), filtered_events)
    return [len(list(j)) for i, j in groupby(sorted(rounded_timestamp))]


def pad_with_zeros(events, start_time, length):
    """Pad list with zeros inserting it into a vector of length."""
    zeros = [0] * length
    zeros[start_time:start_time + len(events)] = events
    return zeros


def process_events_for_plotting(events, start_time, length, clean_up_interval):
    """Prepare vector for plotting."""
    groupped_events = group_events_by_time(events=events,
                                           start_time=start_time,
                                           length=length,
                                           clean_up_interval=clean_up_interval)
    return pad_with_zeros(events=groupped_events,
                          start_time=start_time/clean_up_interval,
                          length=length)


def generate_events(n_events,
                    start_time,
                    end_time,
                    total_time,
                    clean_up_interval,
                    event=""):
    """Generate n_events events.

    Generates n_events from start_time to end_time, for a total_time simulation
    """
    params = [n_events, start_time, end_time]
    assert(all([type(x) == int for x in params]))

    time_window = end_time - start_time
    step = time_window/float(n_events)
    noise_params = 2
    raw_events = []
    if not event:
        print "Generating {} random events [{},{}]. Step {}".format(n_events,
                                                                    start_time,
                                                                    end_time,
                                                                    step)
        # generate random strings
        for i in range(n_events):
            noise = random.normalvariate(noise_params, noise_params/2)

            random_event = ''.join(
                random.choice(string.ascii_uppercase + string.digits)
                for _ in range(10))
            raw_events.append(
                (start_time + i * step + noise, random_event))
    else:
        print "Generating {} events [{},{}]: {}. Step {}".format(n_events,
                                                                 start_time,
                                                                 end_time,
                                                                 event,
                                                                 step)

        for i in range(n_events):
            noise = random.normalvariate(noise_params, noise_params/2)

            raw_events.append(
                (start_time + i * step + noise, event))

    processed_events = process_events_for_plotting(raw_events,
                                                   start_time,
                                                   total_time,
                                                   clean_up_interval)
    print "{} were generated and mapped to {} buckets".\
        format(len(raw_events),
               len(processed_events))
    return raw_events, processed_events


if __name__ == '__main__':
    # Config variables

    total_simulation_time = 60
    clean_up_interval = 3
    buckets = total_simulation_time / clean_up_interval + 1

    plot_events = collections.OrderedDict()
    # 50000 heavy hitters
    hh_events1, hh_events1_groupped =\
        generate_events(1600, 0, 20, total_simulation_time,
                        clean_up_interval, "E1")
    plot_events.update({"E1": hh_events1_groupped})

    hh_events2, hh_events2_groupped =\
        generate_events(800, 10, 40, total_simulation_time,
                        clean_up_interval, "E2")
    plot_events.update({"E2": hh_events2_groupped})

    # 10000 heavy changers
    hc_events1, hc_events1_groupped =\
        generate_events(400, 33, 36, total_simulation_time,
                        clean_up_interval, "E3")
    plot_events.update({"E3": hc_events1_groupped})
    hc_events2, hc_events2_groupped =\
        generate_events(500, 40, 47, total_simulation_time,
                        clean_up_interval, "E4")
    plot_events.update({"E4": hc_events2_groupped})
    hc_events3, hc_events3_groupped =\
        generate_events(400, 54, 58, total_simulation_time,
                        clean_up_interval, "E5")
    plot_events.update({"E5": hc_events3_groupped})

    # noise
    noise_events, noise_events_groupped =\
        generate_events(1500, 0, total_simulation_time, total_simulation_time,
                        clean_up_interval)
    plot_events.update({"Random": noise_events_groupped})

    print [len(e[:buckets]) for e in plot_events.itervalues()]
    mtx = np.stack([e[:buckets] for e in plot_events.itervalues()])

    df = DataFrame(np.transpose(mtx), columns=plot_events.keys())
    ax = df.plot(kind='bar', stacked=True)
    ax.set_xlabel('Tiempo')
    ax.set_ylabel('Cant. de eventos')
    plt.show(block=False)

    all_events = []
    all_events += hh_events1 + hh_events2
    all_events += hc_events1 + hc_events2 + hc_events3
    all_events += noise_events

    schd = sched.scheduler(time.time, time.sleep)

    for e in all_events:
        schd.enter(e[0], 1, post_event, [e[1], e[0]])

    print "Running scheduler"
    t = datetime.now()
    schd.run()
    print "Took {}".format(datetime.now() - t)
    plt.show()
