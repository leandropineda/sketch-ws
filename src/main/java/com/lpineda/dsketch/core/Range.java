package com.lpineda.dsketch.core;

public class Range {
    public Long from, to;

    public static Integer estimateChange(Range range_first_epoch, Range range_second_epoch) {
        Long estimated_change = Math.max(range_first_epoch.to - range_second_epoch.from,
                range_second_epoch.to - range_first_epoch.from);
        return estimated_change.intValue();
    }
}
