package org.etf.coronacity.service.movement;

import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.interfaces.CheckpointMeasurementListener;
import org.etf.coronacity.model.Alarm;
import org.etf.coronacity.model.carrier.AppData;
import org.etf.coronacity.model.LocationData;
import org.etf.coronacity.model.building.Checkpoint;
import org.etf.coronacity.model.person.Person;

import java.util.ArrayList;
import java.util.stream.Collectors;

/*
    Thread that measures temperatures
 */
public class MeasurementThread extends Thread {

    private AppData appData;
    private CheckpointMeasurementListener checkpointMeasurementListener;

    private volatile boolean isRunning;

    public MeasurementThread(AppData appData, CheckpointMeasurementListener checkpointMeasurementListener) {
        this.appData = appData;
        this.checkpointMeasurementListener = checkpointMeasurementListener;

        this.isRunning = true;
    }

    @Override
    public void run() {
        measure();
    }

    /**
     * Stop thread
     */
    public void stopRunning() {
        this.isRunning = false;
    }

    //
    //

    /**
     * Loop through checkpoints
     * Find persons near that checkpoint
     * Check that person temperature
     * If temperature is greater that 37 then that person is infected
     * If person is infected, stop that person and notify other persons from the same house to go home
     */
    private void measure() {

        ArrayList<Checkpoint> checkpoints = Utils.getCheckpoints(appData.getBuildings());

        while (isRunning) {

            for (Checkpoint checkpoint : checkpoints) {

                if (!isRunning)
                    break;

                ArrayList<Person> nearPersons = getPersonsNearCheckpoint(checkpoint.getPositionX(), checkpoint.getPositionY());

                for (Person person : nearPersons)
                    if (person.getBodyTemperature() > Constants.TEMPERATURE_NORMAL && !person.isInfected())
                        // send alarm
                        checkpointMeasurementListener.onAlarmSent(
                                new Alarm(
                                        person.getId(),
                                        person.getLocationData().getPositionX(),
                                        person.getLocationData().getPositionY(),
                                        person.getHomeId()
                                )
                        );
            }
        }
    }

    /**
     * Find persons on positions near checkpoint
     * @param posX checkpoint's position x
     * @param posY checkpoint's position y
     * @return persons that are near the given position
     */
    private ArrayList<Person> getPersonsNearCheckpoint(int posX, int posY) {

        return (ArrayList<Person>)
                appData.getPersons().values().stream()
                        .filter(person -> isNear(posX, posY, person.getLocationData()))
                        .collect(Collectors.toList());
    }

    /**
     * Check if person is new the given position
     * @param posX position x
     * @param posY position y
     * @param locationData person's location data
     * @return true if person is near, otherwise false
     */
    private boolean isNear(int posX, int posY, LocationData locationData) {

        for (int i = posX - 1; i <= posX + 1; i++)
            for (int j = posY - 1; j <= posY + 1; j++)
                if (locationData.getPositionX() == i && locationData.getPositionY() == j)
                    return true;

        return false;
    }
}
