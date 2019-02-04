package com.gardentracker.classes;

public class Maintenance {
    private int id, intervalInDays;
    private String name, description;
    private long lastCheck, nextCheck, changed;

    public Maintenance() {
    }

    public Maintenance(int id, String name, String description, long lastCheck, long nextCheck, int intervalInDays, long changed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lastCheck = lastCheck;
        this.nextCheck = nextCheck;
        this.intervalInDays = intervalInDays;
        this.changed = changed;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public long getLastCheck() {
        return lastCheck;
    }
    public void setLastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
    }

    public long getNextCheck() {
        return nextCheck;
    }
    public void setNextCheck(long nextCheck) {
        this.nextCheck = nextCheck;
    }

    public int getIntervalInDays() {
        return intervalInDays;
    }
    public void setIntervalInDays(int intervalInDays) {
        this.intervalInDays = intervalInDays;
    }

    public long getChanged() {
        return changed;
    }
    public void setChanged(long changed) {
        this.changed = changed;
    }
}
