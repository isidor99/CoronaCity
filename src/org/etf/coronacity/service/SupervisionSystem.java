package org.etf.coronacity.service;

import org.etf.coronacity.interfaces.CheckpointMeasurementListener;
import org.etf.coronacity.model.Alarm;

import java.util.Stack;
import java.util.function.Consumer;

/*
    Supervision system
    Contains stack on which alarms are stored
    When checkpoint detect new infected person, then new alarm is added on the stack
    When ambulance is sent then alarm is popped from the stack
 */
public class SupervisionSystem implements CheckpointMeasurementListener {

    private Stack<Alarm> alarmStack;
    private Consumer<Long> alarmListener;

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
