package com.gardentracker.classes;

public class Settings {

    private int id, notificationOn;
    private long notificationTime, changed;
    private String weatherLanguage, weatherCity, weatherUnits;

    public Settings (int id, int notificationOn, long notificationTime, String weatherLanguage, String weatherCity, String weatherUnits, long changed) {
        this.id = id;
        this.notificationOn = notificationOn;
        this.notificationTime = notificationTime;
        this.weatherLanguage = weatherLanguage;
        this.weatherCity = weatherCity;
        this.weatherUnits = weatherUnits;
        this.changed = changed;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getNotificationOn() {
        return notificationOn;
    }
    public void setNotificationOn(int notificationOn) {
        this.notificationOn = notificationOn;
    }

    public long getNotificationTime() {
        return notificationTime;
    }
    public void setNotificationTime(long notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getWeatherLanguage() {
        return weatherLanguage;
    }
    public void setWeatherLanguage(String weatherLanguage) {
        this.weatherLanguage = weatherLanguage;
    }

    public String getWeatherCity() {
        return weatherCity;
    }
    public void setWeatherCity(String weatherCity) {
        this.weatherCity = weatherCity;
    }

    public String getWeatherUnits() {
        return weatherUnits;
    }
    public void setWeatherUnits(String weatherUnits) {
        this.weatherUnits = weatherUnits;
    }

    public long getChanged() {
        return changed;
    }
    public void setChanged(long changed) {
        this.changed = changed;
    }
}
