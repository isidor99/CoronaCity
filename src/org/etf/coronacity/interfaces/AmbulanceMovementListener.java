package org.etf.coronacity.interfaces;

@FunctionalInterface
public interface AmbulanceMovementListener {

    void onMovementPerformed(int posX, int posY, int prevX, int prevY);
}
