package com.example.clockapp;

import java.io.Serializable;

public class Alarm implements Serializable {
    private int hour;
    private int minute;
    private String label; // Alarm için bir etiket

    // Constructor
    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.label = label;
    }

    // Getter metodları
    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getLabel() {
        return label;
    }

    // Setter metodları (isteğe bağlı)
    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
