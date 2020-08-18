package org.etf.coronacity.model;

import java.io.Serializable;

public class Alarm implements Serializable {

    private static long alarmId = 1;

    private long id;
    private long personId;
    private int positionX;
    private int positionY;
    private long homeId;

    public Alarm(long personId, int positionX, int positionY, long homeId) {
        this.id = alarmId++;
        setPersonId(personId);
        setPositionX(positionX);
        setPositionY(positionY);
        setHomeId(homeId);
    }

    public long getId() { return id; }

    public void setPersonId(long personId) { this.personId = personId; }

    public long getPersonId() { return personId; }

    public void setPositionX(int positionX) { this.positionX = positionX; }

    public int getPositionX() { return positionX; }

    public void setPositionY(int positionY) { this.positionY = positionY; }

    public int getPositionY() { return positionY; }

    public void setHomeId(long homeId) { this.homeId = homeId; }

    public long getHomeId() { return homeId; }
}
