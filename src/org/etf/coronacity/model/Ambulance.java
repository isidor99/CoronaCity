package org.etf.coronacity.model;

import java.io.Serializable;

public class Ambulance implements Serializable {

    private static long ambulanceId = 1;

    private final long id;
    private int positionX;
    private int positionY;
    private boolean busy;
    private long personId;

    public Ambulance() {
        this.id = ambulanceId++;
    }

    public long getId() { return id; }

    public void setPositionX(int positionX) { this.positionX = positionX; }

    public int getPositionX() { return positionX; }

    public void setPositionY(int positionY) { this.positionY = positionY; }

    public int getPositionY() { return positionY; }

    public void setBusy(boolean busy) { this.busy = busy; }

    public boolean isBusy() { return busy; }

    public long getPersonId() { return personId; }

    public void setPersonId(long personId) { this.personId = personId; }

    @Override
    public String toString() {
        return getId() + ", Ambulance, " + getPositionX() + ", " + getPositionY() + ", " + "\n";
    }
}
