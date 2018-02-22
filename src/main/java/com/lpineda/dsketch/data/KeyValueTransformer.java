package com.lpineda.dsketch.data;

import java.util.Set;

public interface KeyValueTransformer {
    Integer getValue(String key);
    Set<String> getEvent(Set<Integer> value);
}
