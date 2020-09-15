package org.etf.coronacity.service.movement;

import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.model.building.Hospital;
import org.etf.coronacity.model.person.Person;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

/*
    Thread that checks temperatures of each infected in each hospital
 */
public class HospitalThread extends Thread {

    private ArrayList<Hospital> hospitals;
    private Consumer<Person> recoverListener;

    public HospitalThread(ArrayList<Hospital> hospitals) {
        this.hospitals = hospitals;
    }


    @Override
    public void run() {

        measureTemperature();
        checkHealth();
    }

    public void setRecoverListener(Consumer<Person> recoverListener) {
        this.recoverListener = recoverListener;
    }

    //
    //

    /**
     * After temperature is changed, store new temperature of person to list
     */
    private void measureTemperature() {

        hospitals.forEach(hospital -> hospital.getInfected()
                .values()
                .forEach(person -> {

                    LinkedList<Double> temperatures = hospital.getTemperaturesList(person.getId());

                    if (temperatures.size() >= 3)
                        temperatures.removeFirst();

                    temperatures.add(person.getBodyTemperature());

                    hospital.setTemperatures(person.getId(), temperatures);
                }));
    }

    /**
     * Check last three temperatures, if average is lower that 37.0, send that person home
     */
    private void checkHealth() {

        hospitals.forEach(hospital -> {

            for (Iterator<Person> iterator = hospital.getInfected().values().iterator(); iterator.hasNext();) {

                Person person = iterator.next();

                LinkedList<Double> temperatures = hospital.getTemperaturesList(person.getId());

                if (temperatures.size() >= 3) {

                    double averageTemperature = Utils.average(temperatures);

                    if (averageTemperature < 37.0) {
                        // infected is recovered
                        recoverListener.accept(person);
                        iterator.remove();
                    }
                }

            }
        });
    }
}
