package com.lpineda.dsketch.db;

import java.util.Set;
import java.util.SortedSet;

public interface KeyValueTransformer {
    Integer getValue(String key);
    Set<String> getEvent(Set<Integer> value);
}
