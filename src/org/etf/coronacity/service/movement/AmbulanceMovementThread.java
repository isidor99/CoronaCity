package org.etf.coronacity.service.movement;

import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.ShortestPath;
import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.interfaces.AmbulanceMovementListener;
import org.etf.coronacity.model.Ambulance;
import org.etf.coronacity.model.carrier.AmbulanceMovementData;
import org.etf.coronacity.model.carrier.AppData;
import org.etf.coronacity.model.LocationData;
import org.etf.coronacity.model.building.Hospital;
import org.etf.coronacity.model.person.Person;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.logging.Logger;

/*
    This thread performs ambulance movement
    For each ambulance new thread is created
 */
public class AmbulanceMovementThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(AmbulanceMovementThread.class.getName());

    private AppData appData;
    private Ambulance ambulance;
    private Person infectedPerson;
    private AmbulanceMovementListener ambulanceMovementListener;

    private boolean isRunning;
    private Consumer<Long> movementListener;

    public AmbulanceMovementThread(AppData appData, Ambulance ambulance, AmbulanceMovementListener ambulanceMovementListener) {

        if (LOGGER.getHandlers() == null || LOGGER.getHandlers().length == 0)
            Utils.createLoggerHandler(LOGGER);

        this.appData = appData;
        this.ambulance = ambulance;
        this.ambulanceMovementListener = ambulanceMovementListener;
        this.isRunning = true;
        this.infectedPerson = appData.getPersons().get(ambulance.getPersonId());
    }

    @Override
    public int hashCode() {
        return Long.valueOf(ambulance.getId()).intValue();
    }

    @Override
    public void run() {

        if (ambulance != null) {

            AmbulanceMovementData movementData = appData.getAmbulanceMovementData().get(ambulance.getId());

            if (movementData == null) {

                Data data = findNearestHospital();

                if (data != null) {

                    // move ambulance to infected user
                    Hospital hospital = appData.getHospitals().get(data.index);

                    ambulance.setPositionX(hospital.getPositionX());
                    ambulance.setPositionY(hospital.getPositionY());

                    // needs to be added here, because before ambulance returns to hospital
                    // another one can be sent and then in hospital can be more persons
                    // than hospital can accept
                    hospital.addInfected(infectedPerson);

                    performMovement(hospital, data.path, data.path.get(0), false);
                }

            } else
                performMovement(
                        movementData.getHospital(),
                        movementData.getPath(),
                        movementData.getNode(),
                        movementData.isDescending()
                );
        }
    }

    public void setMovementListener(Consumer<Long> movementListener) {
        this.movementListener = movementListener;
    }

    /**
     * Stop the thread
     */
    public void stopRunning() { this.isRunning = false; }

    //
    //
    //

    /**
     * Move ambulance
     * @param hospital hospital from which ambulance starts moving
     * @param path path to infected person
     * @param node starting node of path (if simulation was stopped)
     * @param descending indicates if ambulance goes to hospital or from hospital
     */
    private void performMovement(Hospital hospital, LinkedList<ShortestPath.Node> path,
                                 ShortestPath.Node node, boolean descending) {

        if (!descending) {

            moveToUser(path, node, hospital);
            node = path.get(path.size() - 1);
        }

        if (!isRunning)
            return;

        moveToHospital(path, node, hospital);

        if (!isRunning)
            return;

        appData.getAmbulanceMovementData().remove(ambulance.getId());
        ambulance.setPersonId(0);
        ambulance.setBusy(false);
        movementListener.accept(ambulance.getId());
    }

    //
    //

    /**
     * Move ambulance to user
     * @param path ambulance path
     * @param node starting node
     * @param hospital hospital
     */
    private void moveToUser(LinkedList<ShortestPath.Node> path, ShortestPath.Node node, Hospital hospital) {

        // find correct node
        int index = 0;
        for (int i = 0; i < path.size(); i++)
            if (path.get(i).equals(node)) {
                index = i;
                break;
            }

        for (int i = index; i < path.size(); i++) {

            ShortestPath.Node iNode = path.get(i);

            if (!isRunning) {

                AmbulanceMovementData ambulanceMovementData =
                        new AmbulanceMovementData(ambulance.getId(), false, iNode, path, hospital);

                appData.addAmbulanceMovementData(ambulance.getId(), ambulanceMovementData);
                return;
            }

            int prevX = ambulance.getPositionX();
            int prevY = ambulance.getPositionY();

            ambulance.setPositionX(iNode.getX());
            ambulance.setPositionY(iNode.getY());

            ambulanceMovementListener.onMovementPerformed(iNode.getX(), iNode.getY(), prevX, prevY);

            try {
                Thread.sleep(Constants.AMBULANCE_THREAD_SLEEP_TIME);
            } catch (InterruptedException ex) {
                LOGGER.warning(ex.fillInStackTrace().toString());
            }
        }
    }

    /**
     * Move from infected to hospital
     * @param path ambulance path
     * @param node starting node
     * @param hospital hospital
     */
    private void moveToHospital(LinkedList<ShortestPath.Node> path, ShortestPath.Node node, Hospital hospital) {

        int index = path.size() - 2;
        for (int i = path.size() - 2; i >= 0; i--)
            if (path.get(i).equals(node)) {
                index = i;
                break;
            }

        for (int i = index; i >= 0; i--) {

            ShortestPath.Node iNode = path.get(i);

            if (!isRunning) {

                AmbulanceMovementData ambulanceMovementData =
                        new AmbulanceMovementData(ambulance.getId(), true, iNode, path, hospital);

                appData.addAmbulanceMovementData(ambulance.getId(), ambulanceMovementData);
                return;
            }

            int prevX = ambulance.getPositionX();
            int prevY = ambulance.getPositionY();

            ambulance.setPositionX(iNode.getX());
            ambulance.setPositionY(iNode.getY());
            infectedPerson.getLocationData().setPosition(iNode.getX(), iNode.getY());

            ambulanceMovementListener.onMovementPerformed(iNode.getX(), iNode.getY(), prevX, prevY);

            try {
                Thread.sleep(Constants.AMBULANCE_THREAD_SLEEP_TIME);
            } catch (InterruptedException ex) {
                LOGGER.warning(ex.fillInStackTrace().toString());
            }
        }
    }

    /**
     * Find hospital that is nearest to infected person
     * @return object of type Data (inner class)
     */
    private Data findNearestHospital() {

        ArrayList<LinkedList<ShortestPath.Node>> paths = new ArrayList<>();

        synchronized (appData.getHospitals()) {

            appData.getHospitals().forEach(hospital -> {

                if (hospital.getInfectedCount() < hospital.getCapacity()) {

                    paths.add(
                            ShortestPath.shortestPath(
                                    appData.getMatrix(),
                                    new LocationData(hospital.getPositionX(), hospital.getPositionY()),
                                    infectedPerson.getLocationData()
                            )
                    );

                } else
                    paths.add(null);
            });
        }

        int index = -1;
        int size = Integer.MAX_VALUE;

        for (LinkedList<ShortestPath.Node> path : paths) {

            if (path != null) {

                if (path.size() < size) {

                    index = paths.indexOf(path);
                    size = path.size();
                }
            }
        }

        if (index != -1)
            return new Data(paths.get(index), index);

        return null;
    }

    //
    // private helper class
    //

    private static class Data {

        LinkedList<ShortestPath.Node> path;
        int index; // hospital index

        Data(LinkedList<ShortestPath.Node> path, int index) {
            this.path = path;
            this.index = index;
        }
    }
}