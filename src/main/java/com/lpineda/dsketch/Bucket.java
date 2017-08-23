package com.lpineda.dsketch;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Bucket {
    private Long processed_elements, err;
    private Integer max_length;
    private Map<Integer, Long> event_counter;

    public Bucket(Integer max_length_) {
        max_length = max_length_;
        processed_elements = Long.valueOf(0);
        event_counter = new HashMap<>();
        err = Long.valueOf(0);
    }

    public Long size() {
        return processed_elements;
    }

    public Integer keySetSize() {
        return event_counter.keySet().size();
    }

    public Set<Integer> getKeySet() {
        return event_counter.keySet();
    }

    public Long getEventCounterMinValue() {
        Long min = Long.MAX_VALUE;
        for (Map.Entry<Integer, Long> event : event_counter.entrySet()) {
            if (event.getValue() < min) {
                min = event.getValue();
            }
        }
        return min;
    }

    public void addElement(Integer element_) {
        Long value = Long.valueOf(1);
        this.processed_elements += value;
        if (event_counter.containsKey(element_)) {
            Long key_counter = event_counter.get(element_);
            event_counter.put(element_, key_counter + value);
        } else {
            if (event_counter.size() < max_length) {
                event_counter.put(element_, value);
            } else {
                Long d_err;
                if (value < getEventCounterMinValue()) {
                    d_err = value;
                } else {
                    d_err = getEventCounterMinValue();
                }

                err += d_err;
                event_counter.replaceAll((k, v) -> v -= d_err);
                event_counter.entrySet().removeIf(entries -> entries.getValue() <= 0);
            }
        }

    }

    public Range estimateElementFrequency(Integer element_) {
        Range estimated_frequency = new Range();
        if (event_counter.containsKey(element_)) {
            estimated_frequency.from = event_counter.get(element_);
        } else estimated_frequency.from = Long.valueOf(0);
        estimated_frequency.to = estimated_frequency.from + err;

        return estimated_frequency;
    }

}
