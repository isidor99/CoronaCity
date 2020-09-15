package org.etf.coronacity.model.carrier;

import java.io.Serializable;

/*
    This class counts infected and recovered by gender and age
    This class are used for drawing graphs
 */
public class PersonCounter implements Serializable {

    private long infectedMale;
    private long infectedFemale;
    private long infectedChild;
    private long infectedAdult;
    private long infectedOld;
    private long recoveredMale;
    private long recoveredFemale;
    private long recoveredChild;
    private long recoveredAdult;
    private long recoveredOld;

    public PersonCounter() {
        setInfectedMale(0);
        setInfectedFemale(0);
        setInfectedChild(0);
        setInfectedAdult(0);
        setInfectedOld(0);
        setRecoveredMale(0);
        setRecoveredFemale(0);
        setRecoveredChild(0);
        setRecoveredAdult(0);
        setRecoveredOld(0);
    }

    public long getInfectedMale() {
        return infectedMale;
    }

    public void setInfectedMale(long infectedMale) {
        this.infectedMale = infectedMale;
    }

    public void incrementInfectedMale() { this.infectedMale++; }

    public long getInfectedFemale() {
        return infectedFemale;
    }

    public void setInfectedFemale(long infectedFemale) {
        this.infectedFemale = infectedFemale;
    }

    public void incrementInfectedFemale() { this.infectedFemale++; }

    public long getInfectedChild() {
        return infectedChild;
    }

    public void setInfectedChild(long infectedChild) {
        this.infectedChild = infectedChild;
    }

    public void incrementInfectedChild() { this.infectedChild++; }

    public long getInfectedAdult() {
        return infectedAdult;
    }

    public void setInfectedAdult(long infectedAdult) {
        this.infectedAdult = infectedAdult;
    }

    public void incrementInfectedAdult() { this.infectedAdult++; }

    public long getInfectedOld() {
        return infectedOld;
    }

    public void setInfectedOld(long infectedOld) {
        this.infectedOld = infectedOld;
    }

    public void incrementInfectedOld() { this.infectedOld++; }

    public long getRecoveredMale() {
        return recoveredMale;
    }

    public void setRecoveredMale(long recoveredMale) {
        this.recoveredMale = recoveredMale;
    }

    public void incrementRecoveredMale() { this.recoveredMale++; }

    public long getRecoveredFemale() {
        return recoveredFemale;
    }

    public void setRecoveredFemale(long recoveredFemale) {
        this.recoveredFemale = recoveredFemale;
    }

    public void incrementRecoveredFemale() { this.recoveredFemale++; }

    public long getRecoveredChild() {
        return recoveredChild;
    }

    public void setRecoveredChild(long recoveredChild) {
        this.recoveredChild = recoveredChild;
    }

    public void incrementRecoveredChild() { this.recoveredChild++; }

    public long getRecoveredAdult() {
        return recoveredAdult;
    }

    public void setRecoveredAdult(long recoveredAdult) {
        this.recoveredAdult = recoveredAdult;
    }

    public void incrementRecoveredAdult() { this.recoveredAdult++; }

    public long getRecoveredOld() {
        return recoveredOld;
    }

    public void setRecoveredOld(long recoveredOld) {
        this.recoveredOld = recoveredOld;
    }

    public void incrementRecoveredOld() { this.recoveredOld++; }
}
