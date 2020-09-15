package org.etf.coronacity.model.carrier;

import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.Generator;
import org.etf.coronacity.helper.MatrixHelper;
import org.etf.coronacity.model.Ambulance;
import org.etf.coronacity.model.building.Building;
import org.etf.coronacity.model.building.Hospital;
import org.etf.coronacity.model.person.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Data carrier class
 * Here are stored all major data which are shared between multiple threads
 */
public class AppData implements Serializable {

    private Object[][] matrix;
    private HashMap<String, Integer> data;
    private HashMap<Long, Person> persons;
    private HashMap<Long, Building> buildings;
    private ArrayList<Hospital> hospitals;
    private ArrayList<Ambulance> ambulances;
    private long time;
    private long tempTimerTickTime;
    private long serializationTime;
    private HashMap<Long, AmbulanceMovementData> ambulanceMovementDataHashMap;
    private MovementData movementData;
    private HashMap<Long, UrgentMovementData> urgentMovementDataHashMap;

    public AppData(HashMap<String, Integer> data) {

        setData(data);
        setTime(0);
        setMatrix(new Object[data.get(Constants.KEY_MATRIX_SIZE)][data.get(Constants.KEY_MATRIX_SIZE)]);
        setAmbulanceMovementDataHashMap(new HashMap<>());
        setUrgentMovementDataHashMap(new HashMap<>());
        setMovementData(null);

        int capacity = data.get(Constants.KEY_CAPACITY);

        setPersons(Generator.generatePersons(
                data.get(Constants.KEY_NUM_OF_CHILDREN),
                data.get(Constants.KEY_NUM_OF_ADULTS),
                data.get(Constants.KEY_NUM_OF_OLD)
        ));

        setBuildings(Generator.generateBuildings(
                data.get(Constants.KEY_NUM_OF_HOMES),
                data.get(Constants.KEY_NUM_OF_CHECKPOINTS)
        ));

        setAmbulances(Generator.generateAmbulances(data.get(Constants.KEY_NUM_OF_AMBULANCES)));

        MatrixHelper.setHomesAndCheckpoints(matrix, buildings);
        MatrixHelper.setPeopleInHouses(persons, buildings, matrix.length);

        setHospitals(MatrixHelper.createHospitals(matrix, capacity));
    }

    public Object[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(Object[][] matrix) {
        this.matrix = matrix;
    }

    public HashMap<String, Integer> getData() {
        return data;
    }

    public void setData(HashMap<String, Integer> data) {
        this.data = data;
    }

    public HashMap<Long, Person> getPersons() {
        return persons;
    }

    public void setPersons(HashMap<Long, Person> persons) {
        this.persons = persons;
    }

    public HashMap<Long, Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(HashMap<Long, Building> buildings) {
        this.buildings = buildings;
    }

    public ArrayList<Hospital> getHospitals() {
        return hospitals;
    }

    public void setHospitals(ArrayList<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    public ArrayList<Ambulance> getAmbulances() {
        return ambulances;
    }

    public void setAmbulances(ArrayList<Ambulance> ambulances) {
        this.ambulances = ambulances;
    }

    public void setTime(long time) { this.time = time; }

    public long getTime() { return time; }

    public void setTempTimerTickTime(long tempTimerTickTime) { this.tempTimerTickTime = tempTimerTickTime; }

    public long getTempTimerTickTime() { return tempTimerTickTime; }

    public void setSerializationTime(long serializationTime) { this.serializationTime = serializationTime; }

    public long getSerializationTime() { return serializationTime; }

    public void addAmbulanceMovementData(long key, AmbulanceMovementData ambulanceMovementData) {
        ambulanceMovementDataHashMap.put(key, ambulanceMovementData);
    }

    public HashMap<Long, AmbulanceMovementData> getAmbulanceMovementData() {
        return ambulanceMovementDataHashMap;
    }

    public void setAmbulanceMovementDataHashMap(HashMap<Long, AmbulanceMovementData> ambulanceMovementDataHashMap) {
        this.ambulanceMovementDataHashMap = ambulanceMovementDataHashMap;
    }

    public MovementData getMovementData() { return movementData; }

    public void setMovementData(MovementData movementData) { this.movementData = movementData; }

    public HashMap<Long, UrgentMovementData> getUrgentMovementData() { return urgentMovementDataHashMap; }

    public void setUrgentMovementDataHashMap(HashMap<Long, UrgentMovementData> urgentMovementDataHashMap) {
        this.urgentMovementDataHashMap = urgentMovementDataHashMap;
    }
}
