package org.etf.coronacity.model.building;

public class Checkpoint extends Building {

    public Checkpoint() {

    }

    @Override
    public String toString() {

        return "CHP: " + getId() + " (" + getPositionX() + ", " + getPositionY() + ")";
    }
}
