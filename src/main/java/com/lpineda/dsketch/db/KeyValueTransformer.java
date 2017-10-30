package com.lpineda.dsketch.db;

import java.util.Set;
import java.util.SortedSet;

public interface KeyValueTransformer {
    Integer getIntegerFromString(String key);
    Set<String> getStringFromInteger(Set<Integer> values);
}
