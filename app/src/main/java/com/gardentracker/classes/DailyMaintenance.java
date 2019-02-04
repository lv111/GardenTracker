package com.gardentracker.classes;

import java.util.ArrayList;

public class DailyMaintenance {

    private long time;
    private ArrayList<Maintenance> maintenances;

    public DailyMaintenance() {
    }

    public DailyMaintenance(long time, ArrayList<Maintenance> maintenances) {
        this.time = time;
        this.maintenances = maintenances;
    }

    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public ArrayList<Maintenance> getMaintenances() {
        return maintenances;
    }
    public void setMaintenances(ArrayList<Maintenance> maintenances) {
        this.maintenances = maintenances;
    }
}
