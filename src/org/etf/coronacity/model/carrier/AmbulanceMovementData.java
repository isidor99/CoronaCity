package org.etf.coronacity.model.carrier;

import org.etf.coronacity.helper.ShortestPath;
import org.etf.coronacity.model.building.Hospital;

import java.io.Serializable;
import java.util.LinkedList;

public class AmbulanceMovementData implements Serializable {

    private long ambulanceId;
    private boolean descending;
    private ShortestPath.Node node;
    private LinkedList<ShortestPath.Node> path;
    private Hospital hospital;

    public AmbulanceMovementData(long ambulanceId, boolean descending, ShortestPath.Node node,
                                 LinkedList<ShortestPath.Node> path, Hospital hospital) {

        setAmbulanceId(ambulanceId);
        setDescending(descending);
        setNode(node);
        setPath(path);
        setHospital(hospital);
    }

    public long getAmbulanceId() { return ambulanceId; }

    public void setAmbulanceId(long ambulanceId) { this.ambulanceId = ambulanceId; }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public ShortestPath.Node getNode() {
        return node;
    }

    public void setNode(ShortestPath.Node node) {
        this.node = node;
    }

    public LinkedList<ShortestPath.Node> getPath() {
        return path;
    }

    public void setPath(LinkedList<ShortestPath.Node> path) {
        this.path = path;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }
}
