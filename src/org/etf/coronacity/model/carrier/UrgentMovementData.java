package org.etf.coronacity.model.carrier;

import org.etf.coronacity.helper.ShortestPath;

import java.io.Serializable;
import java.util.LinkedList;

public class UrgentMovementData implements Serializable {

    private long personId;
    private LinkedList<ShortestPath.Node> path;
    private int index;

    public UrgentMovementData(long personId, LinkedList<ShortestPath.Node> path) {
        this.personId = personId;
        this.path = path;
        index = 1;
    }

    public void setPersonId(long personId) { this.personId = personId; }

    public long getPersonId() { return personId; }

    public void setPath(LinkedList<ShortestPath.Node> path) { this.path = path; }

    public LinkedList<ShortestPath.Node> getPath() { return path; }

    public ShortestPath.Node getNextNode() {

        if (index < path.size())
            return path.get(index);

        return null;
    }

    public void incrementIndex() { this.index++; }
}
