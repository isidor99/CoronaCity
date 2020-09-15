package org.etf.coronacity.model.carrier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/*
    Class for storing data about infected and recovered
    This data is used for statistics
 */
public class Data implements Serializable {

    private int infected;
    private int recovered;
    private int infectedMale;
    private int infectedFemale;
    private int recoveredMale;
    private int recoveredFemale;
    private int infectedChild;
    private int infectedAdult;
    private int infectedOld;
    private int recoveredChild;
    private int recoveredAdult;
    private int recoveredOld;
    private ArrayList<Long> infectedIds;
    private ArrayList<Long> recoveredIds;
    private HashMap<Long, PersonCounter> measurementAtTime;

    public Data() {
        setInfected(0);
        setRecovered(0);
        setInfectedMale(0);
        setInfectedFemale(0);
        setRecoveredMale(0);
        setRecoveredFemale(0);
        setInfectedChild(0);
        setInfectedAdult(0);
        setInfectedOld(0);
        setRecoveredChild(0);
        setRecoveredAdult(0);
        setRecoveredOld(0);
        setInfectedIds(new ArrayList<>());
        setRecoveredIds(new ArrayList<>());
        setMeasurementAtTime(new HashMap<>());
    }

    public int getInfected() {
        return infected;
    }

    public void setInfected(int infected) {
        this.infected = infected;
    }

    public void incrementInfected() {
        this.infected++;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public void incrementRecovered() {
        this.recovered++;
    }

    public int getInfectedMale() {
        return infectedMale;
    }

    public void setInfectedMale(int infectedMale) {
        this.infectedMale = infectedMale;
    }

    public void incrementInfectedMale() {
        this.infectedMale++;
    }

    public int getInfectedFemale() {
        return infectedFemale;
    }

    public void setInfectedFemale(int infectedFemale) {
        this.infectedFemale = infectedFemale;
    }

    public void incrementInfectedFemale() {
        this.infectedFemale++;
    }

    public int getRecoveredMale() {
        return recoveredMale;
    }

    public void setRecoveredMale(int recoveredMale) {
        this.recoveredMale = recoveredMale;
    }

    public void incrementRecoveredMale() {
        this.recoveredMale++;
    }

    public int getRecoveredFemale() {
        return recoveredFemale;
    }

    public void setRecoveredFemale(int recoveredFemale) {
        this.recoveredFemale = recoveredFemale;
    }

    public void incrementRecoveredFemale() {
        this.recoveredFemale++;
    }

    public int getInfectedChild() {
        return infectedChild;
    }

    public void setInfectedChild(int infectedChild) {
        this.infectedChild = infectedChild;
    }

    public void incrementInfectedChild() {
        this.infectedChild++;
    }

    public int getInfectedAdult() {
        return infectedAdult;
    }

    public void setInfectedAdult(int infectedAdult) {
        this.infectedAdult = infectedAdult;
    }

    public void incrementInfectedAdult() {
        this.infectedAdult++;
    }

    public int getInfectedOld() {
        return infectedOld;
    }

    public void setInfectedOld(int infectedOld) {
        this.infectedOld = infectedOld;
    }

    public void incrementInfectedOld() {
        this.infectedOld++;
    }

    public int getRecoveredChild() {
        return recoveredChild;
    }

    public void setRecoveredChild(int recoveredChild) {
        this.recoveredChild = recoveredChild;
    }

    public void incrementRecoveredChild() {
        this.recoveredChild++;
    }

    public int getRecoveredAdult() {
        return recoveredAdult;
    }

    public void setRecoveredAdult(int recoveredAdult) {
        this.recoveredAdult = recoveredAdult;
    }

    public void incrementRecoveredAdult() {
        this.recoveredAdult++;
    }

    public int getRecoveredOld() {
        return recoveredOld;
    }

    public void setRecoveredOld(int recoveredOld) {
        this.recoveredOld = recoveredOld;
    }

    public void incrementRecoveredOld() {
        this.recoveredOld++;
    }

    public ArrayList<Long> getInfectedIds() {
        return infectedIds;
    }

    public void setInfectedIds(ArrayList<Long> infectedIds) {
        this.infectedIds = infectedIds;
    }

    public ArrayList<Long> getRecoveredIds() {
        return recoveredIds;
    }

    public void setRecoveredIds(ArrayList<Long> recoveredIds) {
        this.recoveredIds = recoveredIds;
    }

    public HashMap<Long, PersonCounter> getMeasurementAtTime() {
        return measurementAtTime;
    }

    public void setMeasurementAtTime(HashMap<Long, PersonCounter> measurementAtTime) {
        this.measurementAtTime = measurementAtTime;
    }

    public void addMeasurementAtTime(long key, PersonCounter personCounter) {
        measurementAtTime.put(key, personCounter);
    }
}
