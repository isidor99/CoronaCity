package org.etf.coronacity.model.carrier;

import java.io.Serializable;

public class MovementData implements Serializable {

    private long personId;

    public MovementData(long personId) {
        this.personId = personId;
    }

    public void setPersonId(long personId) { this.personId = personId; }

    public long getPersonId() { return personId; }
}
