package org.etf.coronacity.model.building;

import org.etf.coronacity.model.person.Person;

import java.util.HashMap;
import java.util.LinkedList;

public class Hospital extends Building {

    private String name;
    private int capacity;
    private HashMap<Long, Person> infected;
    private HashMap<Long, LinkedList<Double>> temperatures;

    public Hospital(String name, int capacity) {
        this(name, capacity, -1, -1);
    }

    public Hospital(String name, int capacity, int positionX, int positionY) {
        super(positionX, positionY);

        this.name = name;
        this.capacity = capacity;
        this.infected = new HashMap<>();
        this.temperatures = new HashMap<>();
    }

    public void setName(String name) { this.name = name; }

    public String getName() { return this.name; }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void addInfected(Person person) {

        infected.put(person.getId(), person);

        LinkedList<Double> temperaturesList = new LinkedList<>();
        temperaturesList.add(person.getBodyTemperature());

        temperatures.put(person.getId(), temperaturesList);
    }

    public int getInfectedCount() {
        return infected.size();
    }

    public HashMap<Long, Person> getInfected() { return infected; }

    public LinkedList<Double> getTemperaturesList(long personId) { return temperatures.get(personId); }

    public void setTemperatures(long personId, LinkedList<Double> temperatures) { this.temperatures.put(personId, temperatures); }

    @Override
    public String toString() {
        return name + ", " + capacity;
    }

}
