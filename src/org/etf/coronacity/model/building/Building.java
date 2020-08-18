package org.etf.coronacity.model.building;

import java.io.Serializable;

public abstract class Building implements Serializable {

    private static long buildingId = 1;

    private final long id;
    private int positionX;
    private int positionY;

    public Building() {
        this.id = buildingId++;
    }

    public Building(int positionX, int positionY) {
        this.id = buildingId++;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public long getId() {
        return id;
    }

    public int getPositionX() { return positionX; }

    public void setPositionX(int positionX) { this.positionX = positionX; }

    public int getPositionY() { return positionY; }

    public void setPositionY(int positionY) { this.positionY = positionY; }
}
