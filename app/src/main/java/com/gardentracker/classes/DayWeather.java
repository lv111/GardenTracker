package com.gardentracker.classes;

public class DayWeather {

    private long time;
    private int humidityInPercent, windDirectionInDegrees, cloudsCoverageInPercent;
    private String weather, weatherDescription, icon;
    private double maxTemperature, minTemperature, dayTemperature, nightTemperature, morningTemperature, eveningTemperature, pressure, windSpeed;

    public DayWeather() {
    }

    public DayWeather(long time, String weather, String weatherDescription, String icon, double maxTemperature, double minTemperature, double dayTemperature, double nightTemperature,
                      double morningTemperature, double eveningTemperature, double pressure, int humidityInPercent, double windSpeed, int windDirectionInDegrees, int cloudsCoverageInPercent) {
        this.time = time;
        this.weather = weather;
        this.weatherDescription = weatherDescription;
        this.icon = icon;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.dayTemperature = dayTemperature;
        this.nightTemperature = nightTemperature;
        this.morningTemperature = morningTemperature;
        this.eveningTemperature = eveningTemperature;
        this.pressure = pressure;
        this.humidityInPercent = humidityInPercent;
        this.windSpeed = windSpeed;
        this.windDirectionInDegrees = windDirectionInDegrees;
        this.cloudsCoverageInPercent = cloudsCoverageInPercent;
    }

    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public String getWeather() {
        return weather;
    }
    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }
    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }
    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }
    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getDayTemperature() {
        return dayTemperature;
    }
    public void setDayTemperature(double dayTemperature) {
        this.dayTemperature = dayTemperature;
    }

    public double getNightTemperature() {
        return nightTemperature;
    }
    public void setNightTemperature(double nightTemperature) {
        this.nightTemperature = nightTemperature;
    }

    public double getMorningTemperature() {
        return morningTemperature;
    }
    public void setMorningTemperature(double morningTemperature) {
        this.morningTemperature = morningTemperature;
    }

    public double getEveningTemperature() {
        return eveningTemperature;
    }
    public void setEveningTemperature(double eveningTemperature) {
        this.eveningTemperature = eveningTemperature;
    }

    public double getPressure() {
        return pressure;
    }
    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public int getHumidityInPercent() {
        return humidityInPercent;
    }
    public void setHumidityInPercent(int humidityInPercent) {
        this.humidityInPercent = humidityInPercent;
    }

    public double getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindDirectionInDegrees() {
        return windDirectionInDegrees;
    }
    public void setWindDirectionInDegrees(int windDirectionInDegrees) {
        this.windDirectionInDegrees = windDirectionInDegrees;
    }

    public int getCloudsCoverageInPercent() {
        return cloudsCoverageInPercent;
    }
    public void setCloudsCoverageInPercent(int cloudsCoverageInPercent) {
        this.cloudsCoverageInPercent = cloudsCoverageInPercent;
    }
}
