package com.gardentracker.classes;

import java.util.ArrayList;

public class Weather {

    private String cityName, country;
    private double cityCoordLat, cityCoordLon;
    private ArrayList<DayWeather> dayWeathers;

    public Weather() {
    }

    public Weather(String cityName, String country, double cityCoordLat, double cityCoordLon, ArrayList<DayWeather> dayWeathers) {
        this.cityName = cityName;
        this.country = country;
        this.cityCoordLat = cityCoordLat;
        this.cityCoordLon = cityCoordLon;
        this.dayWeathers = dayWeathers;
    }

    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public double getCityCoordLat() {
        return cityCoordLat;
    }
    public void setCityCoordLat(double cityCoordLat) {
        this.cityCoordLat = cityCoordLat;
    }

    public double getCityCoordLon() {
        return cityCoordLon;
    }
    public void setCityCoordLon(double cityCoordLon) {
        this.cityCoordLon = cityCoordLon;
    }

    public ArrayList<DayWeather> getDayWeathers() {
        return dayWeathers;
    }
    public void setDayWeathers(ArrayList<DayWeather> dayWeathers) {
        this.dayWeathers = dayWeathers;
    }
}
