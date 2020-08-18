package org.etf.coronacity.service;

import org.etf.coronacity.interfaces.CheckpointMeasurementListener;
import org.etf.coronacity.model.Alarm;

import java.util.Stack;
import java.util.function.Consumer;

public class SupervisionSystem implements CheckpointMeasurementListener {

    private Stack<Alarm> alarmStack;
    private Consumer<Long> alarmListener;
    // private AlarmListener alarmListener;

    public SupervisionSystem() {
        this.alarmStack = new Stack<>();
    }

    @Override
    public void onAlarmSent(Alarm alarm) {

        alarmListener.accept(alarm.getPersonId());
        alarmStack.push(alarm);
    }

    public void setAlarmListener(Consumer<Long> alarmListener) { this.alarmListener = alarmListener; }

    public Alarm pop() {
        return alarmStack.pop();
    }

    public boolean isStackEmpty() {
        return alarmStack.empty();
    }
}
