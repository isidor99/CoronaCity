package org.etf.coronacity.interfaces;

import org.etf.coronacity.model.Alarm;

@FunctionalInterface
public interface CheckpointMeasurementListener {

    void onAlarmSent(Alarm alarm);
}
