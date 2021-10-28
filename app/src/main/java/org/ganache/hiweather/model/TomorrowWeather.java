package org.ganache.hiweather.model;

public class TomorrowWeather {
    private String day;
    private String time;
    private String weather;
    private String tomoTmp;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTomoTmp() {
        return tomoTmp;
    }

    public void setTomoTmp(String tomoTmp) {
        this.tomoTmp = tomoTmp;
    }
}
