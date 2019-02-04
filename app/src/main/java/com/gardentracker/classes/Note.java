package com.gardentracker.classes;

import java.util.ArrayList;

public class Note {
    private int id;

    private String description;
    private long changed;

    public Note() {
    }

    public Note(int id, String description, long changed) {
        this.id = id;
        this.description = description;
        this.changed = changed;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public long getChanged() {
        return changed;
    }
    public void setChanged(long changed) {
        this.changed = changed;
    }
}
