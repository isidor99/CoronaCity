package org.etf.coronacity.service.timer;

import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.interfaces.TemperatureUpdateListener;
import org.etf.coronacity.model.person.Person;

import java.util.HashMap;
import java.util.TimerTask;
import java.util.Random;

public class TemperatureTimerTask extends TimerTask {

    private TemperatureUpdateListener temperatureUpdateListener;
    private HashMap<Long, Person> persons;

    public TemperatureTimerTask(TemperatureUpdateListener temperatureUpdateListener, HashMap<Long, Person> persons) {

        this.temperatureUpdateListener = temperatureUpdateListener;
        this.persons = persons;
    }


    @Override
    public void run() {

        calculateTemperature();
        temperatureUpdateListener.onTemperatureUpdated();
    }

    /**
     * Every 30 seconds calculate new temperature for each person
     */
    private synchronized void calculateTemperature() {

        Random random = new Random();
        int[] sign = {-1, 1};

        for (Person p : persons.values()) {

            double temp = p.getBodyTemperature();
            int sgn = sign[random.nextInt(2)];
            p.setBodyTemperature(Utils.getBodyTemperatureFromRange(temp, sgn));
        }
    }
}
