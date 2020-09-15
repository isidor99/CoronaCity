package org.etf.coronacity.interfaces;

@FunctionalInterface
public interface MovementListener {
    void onMovementPerformed(String message, boolean highlight);
}
