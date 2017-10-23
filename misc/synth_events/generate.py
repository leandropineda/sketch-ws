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
from itertools import groupby
from pandas import DataFrame


def post_event(data, ts, print_event):
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
    if print_event:
        print ts, data


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
    print "Len groupped_events " + str(len(groupped_events))
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
    raw_events = []
    if not event:
        print "Generating {} random events [{},{}]. Step {}".format(n_events,
                                                                    start_time,
                                                                    end_time,
                                                                    step)
        # generate random strings
        for i in range(n_events):
            noise = random.normalvariate(step, step/2)

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
            noise = random.normalvariate(step, step/2)

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

    total_simulation_time = 40
    clean_up_interval = 3
    buckets = total_simulation_time / clean_up_interval + 1

    schd = sched.scheduler(time.time, time.sleep)
    plot_events = collections.OrderedDict()
    # 50000 heavy hitters
    hh_events1, hh_events1_groupped =\
        generate_events(200, 0, 10, total_simulation_time,
                        clean_up_interval, "HH1")
    plot_events.update({"HH1": hh_events1_groupped})

    hh_events2, hh_events2_groupped =\
        generate_events(300, 10, 20, total_simulation_time,
                        clean_up_interval, "HH2")
    plot_events.update({"HH2": hh_events2_groupped})

    # 10000 heavy changers
    hc_events1, hc_events1_groupped =\
        generate_events(151, 12, 16, total_simulation_time,
                        clean_up_interval, "HC1")
    plot_events.update({"HC1": hc_events1_groupped})
    hc_events2, hc_events2_groupped =\
        generate_events(200, 4, 9, total_simulation_time,
                        clean_up_interval, "HC2")
    plot_events.update({"HC2": hc_events2_groupped})
    hc_events3, hc_events3_groupped =\
        generate_events(500, 30, 37, total_simulation_time,
                        clean_up_interval, "HC3")
    plot_events.update({"HC3": hc_events3_groupped})

    # noise
    noise_events, noise_events_groupped =\
        generate_events(200, 0, total_simulation_time, total_simulation_time,
                        clean_up_interval)
    plot_events.update({"Noise": noise_events_groupped})

    print [len(e[:buckets]) for e in plot_events.itervalues()]
    mtx = np.stack([e[:buckets] for e in plot_events.itervalues()])

    df = DataFrame(np.transpose(mtx), columns=plot_events.keys())
    df.plot(kind='bar', stacked=True)
    plt.show(block=False)

    all_events = []
    all_events += hh_events1 + hh_events2
    all_events += hc_events1 + hc_events2 + hc_events3
    all_events += noise_events

    for e in all_events:
        schd.enter(e[0], 1, post_event, [e[1], e[0], True])

    print "Running scheduler"
    schd.run()
    plt.show()
