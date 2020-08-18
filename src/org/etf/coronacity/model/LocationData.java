package org.etf.coronacity.model;

import java.io.Serializable;

public class LocationData implements Serializable {

    private int positionX;
    private int positionY;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private Direction direction;


    public LocationData() {

    }

    public LocationData(int positionX, int positionY) {

        this.positionX = positionX;
        this.positionY = positionY;
    }

    public int getPositionX() { return positionX; }

    public void setPositionX(int positionX) { this.positionX = positionX; }

    public int getPositionY() { return positionY; }

    public void setPositionY(int positionY) { this.positionY = positionY; }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setPosition(int positionX, int positionY) {
        setPositionX(positionX);
        setPositionY(positionY);
    }

    //
    // Directions enum
    //
    public enum Direction { RIGHT, RIGHT_BOTTOM, BOTTOM, LEFT_BOTTOM, LEFT, LEFT_TOP, TOP, RIGHT_TOP }
}


