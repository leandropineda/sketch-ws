package com.lpineda.dsketch.db;

import java.util.Set;

public interface KeyValueTransformer {
    Integer getIntegerFromString(String key);
    Set<String> getStringFromInteger(Set<Integer> values);
}
