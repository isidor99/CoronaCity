package org.etf.coronacity.model.building;

public class Home extends Building {

    private int hosts;

    public Home() {
        this.hosts = 0;
    }

    public int getHosts() {
        return hosts;
    }

    public void setHosts(int hosts) {
        this.hosts = hosts;
    }

    public void addOneHost() {
        ++this.hosts;
    }
}
