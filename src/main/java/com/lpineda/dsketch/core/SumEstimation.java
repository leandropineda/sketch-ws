package com.lpineda.dsketch.core;

public class SumEstimation {
    public Long from, to;

    public static Integer estimateChange(SumEstimation sumEstimation_first_epoch, SumEstimation sumEstimation_second_epoch) {
        Long estimated_change = Math.max(sumEstimation_first_epoch.to - sumEstimation_second_epoch.from,
                sumEstimation_second_epoch.to - sumEstimation_first_epoch.from);
        return estimated_change.intValue();
    }
}
