package com.gardentracker.classes;

import android.net.Uri;

import java.sql.Blob;
import java.util.ArrayList;

public class Photo {
    private int id, deleted;
    private String name, photoUri;
    private ArrayList<PhotoDescription> descriptions;
    private byte[] miniature;
    private long changed;

    public Photo() {
    }

    public Photo(int id, String name, ArrayList<PhotoDescription> descriptions, String photoUri, byte[] miniature, int deleted, long changed) {
        this.id = id;
        this.name = name;
        this.descriptions = descriptions;
        this.photoUri = photoUri;
        this.miniature = miniature;
        this.deleted = deleted;
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

    public ArrayList<PhotoDescription> getDescriptions() {
        return descriptions;
    }
    public void setDescriptions(ArrayList<PhotoDescription> descriptions) {
        this.descriptions = descriptions;
    }
    public void addDescriptions(PhotoDescription description) {
        descriptions.add(description);
    }

    public String getPhotoUri() {
        return photoUri;
    }
    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public byte[] getMiniature() {
        return miniature;
    }
    public void setMiniature(byte[] miniature) {
        this.miniature = miniature;
    }

    public int getDeleted() {
        return deleted;
    }
    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public long getChanged() {
        return changed;
    }
    public void setChanged(long changed) {
        this.changed = changed;
    }
}
