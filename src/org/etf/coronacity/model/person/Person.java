package org.etf.coronacity.model.person;

import org.etf.coronacity.model.LocationData;

import java.io.Serializable;

public abstract class Person implements Serializable {

    private static long personId = 1;

    private final long id;
    private String name;
    private String surname;
    private long birthYear;
    private Gender gender;
    private long homeId;
    private double bodyTemperature;
    private LocationData locationData;
    private boolean move;
    private boolean infected;
    /*private boolean inHospital;
    private boolean inAmbulance;
    private boolean inHome;*/

    public Person() {

        this.id = personId++;
        setMove(true);
        setInfected(false);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(long birthYear) {
        this.birthYear = birthYear;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public long getHomeId() {
        return homeId;
    }

    public void setHomeId(long homeId) {
        this.homeId = homeId;
    }

    public double getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(double bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public LocationData getLocationData() { return locationData; }

    public void setLocationData(LocationData locationData) { this.locationData = locationData; }

    public boolean canMove() { return move; }

    public void setMove(boolean move) { this.move = move; }

    public boolean isInfected() { return infected; }

    public void setInfected(boolean infected) { this.infected = infected; }

    @Override
    public String toString() {

        return getId() + ", " + getName() + ", " + getSurname() + ", " + getBirthYear() + ", " +
                getGender().toString() + ", " + getBodyTemperature() + ", " + homeId + ", " +
                locationData.getPositionX() + ", " + locationData.getPositionY() + ", " +
                locationData.getDirection().toString() + ", " + isInfected() + "\n";
    }


    // Gender enum
    public enum Gender { MALE, FEMALE }
}
