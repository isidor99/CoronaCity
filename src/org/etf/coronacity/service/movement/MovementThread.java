package org.etf.coronacity.service.movement;

import org.etf.coronacity.helper.*;
import org.etf.coronacity.interfaces.MovementListener;
import org.etf.coronacity.model.carrier.UrgentMovementData;
import org.etf.coronacity.model.carrier.AppData;
import org.etf.coronacity.model.LocationData;
import org.etf.coronacity.model.carrier.MovementData;
import org.etf.coronacity.model.building.Checkpoint;
import org.etf.coronacity.model.building.Home;
import org.etf.coronacity.model.person.Adult;
import org.etf.coronacity.model.person.Child;
import org.etf.coronacity.model.person.Old;
import org.etf.coronacity.model.person.Person;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

public class MovementThread extends Thread {

    private static final Logger LOGGER  = Logger.getLogger(MovementThread.class.getName());

    private AppData appData;
    private MovementListener movementListener;

    private boolean isRunning;

    public MovementThread(AppData appData, MovementListener movementListener) {
        this.appData = appData;
        this.movementListener = movementListener;

        this.isRunning = true;

        Utils.createLoggerHandler(LOGGER);
    }


    @Override
    public void run() {

        if (appData.getMovementData() != null)
            performMovement(appData.getMovementData().getPersonId());
        else
            performMovement(appData.getPersons().values().stream().findFirst().get().getId());
    }

    public void stopRunning() {
        this.isRunning = false;
    }

    //
    private void performMovement(long personId) {

        while (isRunning) {

            for (Iterator<Person> iterator = appData.getPersons().values().iterator(); iterator.hasNext();) {

                Person person = iterator.next();
                if (person.getId() < personId)
                    continue;

                if (!isRunning) {

                    MovementData movementData = new MovementData(person.getId());
                    appData.setMovementData(movementData);

                    break;
                }

                if (person.canMove() && checkMovement(person) && checkBuilding(person) && checkDistance(person)) {

                    int prevX = person.getLocationData().getPositionX();
                    int prevY = person.getLocationData().getPositionY();

                    move(person);
                    movementListener.onMovementPerformed(person.toString());

                    try {
                        Thread.sleep(Constants.PERSON_THREAD_SLEEP_TIME);
                    } catch (InterruptedException ex) {
                        LOGGER.warning(ex.fillInStackTrace().toString());
                    }

                } else if (person.canMove()) {

                    LocationData.Direction[] personDirections = { person.getLocationData().getDirection() };

                    // change direction
                    for (LocationData.Direction dir : LocationData.Direction.values()) {

                        if (Arrays.stream(personDirections).noneMatch(direction -> direction == dir)) {

                            person.getLocationData().setDirection(dir);

                            if (checkMovement(person) && checkDistance(person)) {

                                int prevX = person.getLocationData().getPositionX();
                                int prevY = person.getLocationData().getPositionY();

                                move(person);
                                movementListener.onMovementPerformed(person.toString());

                                try {
                                    Thread.sleep(Constants.PERSON_THREAD_SLEEP_TIME);
                                } catch (InterruptedException ex) {
                                    LOGGER.warning(ex.fillInStackTrace().toString());
                                }

                                break;
                            }
                        }
                    }

                } else if (!person.canMove() && !person.isInfected()) {

                    int prevX = person.getLocationData().getPositionX();
                    int prevY = person.getLocationData().getPositionY();

                    UrgentMovementData data = appData.getUrgentMovementData().get(person.getId());

                    if (data != null) {

                        ShortestPath.Node node = data.getNextNode();

                        if (node != null && checkDistance(person)) {

                            data.incrementIndex();
                            person.getLocationData().setPosition(node.getX(), node.getY());

                            LocationData.Direction direction =
                                    MatrixHelper.getPersonDirection(
                                            prevX,
                                            prevY,
                                            node.getX(),
                                            node.getY());

                            if (direction != null)
                                person.getLocationData().setDirection(direction);

                            movementListener.onMovementPerformed("URGENT: " + person.toString() );

                        } else
                            appData.getUrgentMovementData().remove(person.getId());
                    }
                }
            }

            personId = appData.getPersons().values().stream().findFirst().get().getId();

            try {
                Thread.sleep(Constants.PERSON_THREAD_SLEEP_TIME);
            } catch (InterruptedException ex) {
                LOGGER.warning(ex.fillInStackTrace().toString());
            }
        }
    }

    private boolean checkMovement(Person person) {

        LocationData locationData = person.getLocationData();

        switch (person.getLocationData().getDirection()) {

            case RIGHT:
                return locationData.getPositionY() < locationData.getMaxY();

            case RIGHT_BOTTOM:
                return locationData.getPositionX() < locationData.getMaxX() &&
                        locationData.getPositionY() < locationData.getMaxY();

            case BOTTOM:
                return locationData.getPositionX() < locationData.getMaxX();

            case LEFT_BOTTOM:
                return locationData.getPositionX() < locationData.getMaxX() &&
                        locationData.getPositionY() > locationData.getMinY();

            case LEFT:
                return locationData.getPositionY() > locationData.getMinY();

            case LEFT_TOP:
                return locationData.getPositionX() > locationData.getMinX() &&
                        locationData.getPositionY() > locationData.getMinY();

            case TOP:
                return locationData.getPositionX() > locationData.getMinX();

            case RIGHT_TOP:
                return locationData.getPositionX() > locationData.getMinX() &&
                        locationData.getPositionY() < locationData.getMaxY();
        }

        return false;
    }

    private boolean checkDistance(Person person) {

        int posX = person.getLocationData().getPositionX();
        int posY = person.getLocationData().getPositionY();

        int fromX, toX, fromY, toY;

        switch (person.getLocationData().getDirection()) {

            case RIGHT:
                fromX = posX - 2;
                toX = posX + 2;
                fromY = posY + 1;
                toY = posY + 3;
                break;

            case RIGHT_BOTTOM:
                fromX = posX + 1;
                toX = posX + 3;
                fromY = posY - 1;
                toY = posY + 3;
                break;

            case BOTTOM:
                fromX = posX + 1;
                toX = posX + 3;
                fromY = posY - 2;
                toY = posY + 2;
                break;

            case LEFT_BOTTOM:
                fromX = posX + 1;
                toX = posX + 3;
                fromY = posY - 3;
                toY = posY + 1;
                break;

            case LEFT:
                fromX = posX - 2;
                toX = posX + 2;
                fromY = posY - 3;
                toY = posY - 2;
                break;

            case LEFT_TOP:
                fromX = posX - 3;
                toX = posX - 1;
                fromY = posY - 3;
                toY = posY + 1;
                break;

            case TOP:
                fromX = posX - 3;
                toX = posX - 1;
                fromY = posY - 2;
                toY = posY + 2;
                break;

            case RIGHT_TOP:
                fromX = posX - 3;
                toX = posX - 1;
                fromY = posY - 1;
                toY = posY + 3;
                break;

            default:
                fromX = toX = fromY = toY = 0;
        }


        for (int i = fromX; i <= toX; i++)
            for (int j = fromY; j <= toY; j++)
                if (isPersonNear(i, j, person))
                    return false;


        return true;
    }


    private boolean checkBuilding(Person person) {

        int posX = person.getLocationData().getPositionX();
        int posY = person.getLocationData().getPositionY();

        int newX = 0, newY = 0;

        switch (person.getLocationData().getDirection()) {

            case RIGHT:
                newX = posX;
                newY = posY + 1;
                break;

            case RIGHT_BOTTOM:
                newX = posX + 1;
                newY = posY+ 1;
                break;

            case BOTTOM:
                newX = posX + 1;
                newY = posY;
                break;

            case LEFT_BOTTOM:
                newX = posX + 1;
                newY = posY - 1;
                break;

            case LEFT:
                newX = posX;
                newY = posY - 1;
                break;

            case LEFT_TOP:
                newX = posX - 1;
                newY = posY - 1;
                break;

            case TOP:
                newX = posX - 1;
                newY = posY;
                break;

            case RIGHT_TOP:
                newX = posX - 1;
                newY = posY + 1;
                break;
        }

        Object onNewPos = appData.getMatrix()[newX][newY];
        return onNewPos == null || onNewPos instanceof Checkpoint || (onNewPos instanceof Home && ((Home) onNewPos).getId() == person.getHomeId());
    }


    private void move(Person person) {

        int posX = person.getLocationData().getPositionX();
        int posY = person.getLocationData().getPositionY();

        switch (person.getLocationData().getDirection()) {

            case RIGHT:
                person.getLocationData().setPositionY(posY + 1);
                return;

            case RIGHT_BOTTOM:
                person.getLocationData().setPosition(posX + 1, posY + 1);
                return;

            case BOTTOM:
                person.getLocationData().setPositionX(posX + 1);
                return;

            case LEFT_BOTTOM:
                person.getLocationData().setPosition(posX + 1, posY - 1);
                return;

            case LEFT:
                person.getLocationData().setPositionY(posY - 1);
                return;

            case LEFT_TOP:
                person.getLocationData().setPosition(posX - 1, posY - 1);
                return;

            case TOP:
                person.getLocationData().setPositionX(posX - 1);
                return;

            case RIGHT_TOP:
                person.getLocationData().setPosition(posX - 1, posY + 1);
        }
    }

    /**
        Loop through all persons and check if there is any person near
        Persons from same home can be on near
     */
    private boolean isPersonNear(int i, int j, Person person) {

        return appData.getPersons().values().stream()
                .anyMatch(p -> {

                    if (person instanceof Child)
                        return p.getLocationData().getPositionX() == i &&
                                p.getLocationData().getPositionY() == j &&
                                p.getHomeId() != person.getHomeId() &&
                                p instanceof Old;

                    else if (person instanceof Adult)
                        return p.getLocationData().getPositionX() == i &&
                                p.getLocationData().getPositionY() == j &&
                                p.getHomeId() != person.getHomeId() &&
                                !(p instanceof Child);

                    else
                        return p.getLocationData().getPositionX() == i &&
                                p.getLocationData().getPositionY() == j &&
                                p.getHomeId() != person.getHomeId();

                });
    }
}
