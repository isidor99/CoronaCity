package org.etf.coronacity.service.movement;

import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.ShortestPath;
import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.interfaces.MovementListener;
import org.etf.coronacity.model.carrier.AppData;
import org.etf.coronacity.model.LocationData;
import org.etf.coronacity.model.person.Person;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.logging.Logger;

/*
    This thread isn't used
 */

@Deprecated
public class UrgentMovementThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(UrgentMovementThread.class.getName());

    private AppData appData;
    private Person person;
    private MovementListener movementListener;

    private boolean isRunning;
    private Consumer<Long> urgentMovementListener;

    public UrgentMovementThread(AppData appData, Person person, MovementListener movementListener) {
        this.appData = appData;
        this.person = person;
        this.movementListener = movementListener;

        isRunning = true;

        if (LOGGER.getHandlers() == null || LOGGER.getHandlers().length == 0)
            Utils.createLoggerHandler(LOGGER);
    }

    @Override
    public void run() {

        LocationData homePosition =
                new LocationData(
                        appData.getBuildings().get(person.getHomeId()).getPositionX(),
                        appData.getBuildings().get(person.getHomeId()).getPositionY()
                );

        LinkedList<ShortestPath.Node> path =
                ShortestPath.shortestPath(
                        appData.getMatrix(),
                        person.getLocationData(),
                        homePosition
                );

        // remove position where is the person standing
        path.removeFirst();

        for (ShortestPath.Node node : path) {

            if (!isRunning)
                return;

            int prevX = person.getLocationData().getPositionX();
            int prevY = person.getLocationData().getPositionY();

            person.getLocationData().setPosition(node.getX(), node.getY());
            movementListener.onMovementPerformed(person.toString(), false);

            try {
                Thread.sleep(Constants.PERSON_THREAD_SLEEP_TIME);
            } catch (InterruptedException ex) {
                LOGGER.warning(ex.fillInStackTrace().toString());
            }
        }

        urgentMovementListener.accept(person.getId());
    }

    public void stopRunning() { this.isRunning = false; }

    public void setUrgentMovementListener(Consumer<Long> urgentMovementListener) {
        this.urgentMovementListener = urgentMovementListener;
    }
}
