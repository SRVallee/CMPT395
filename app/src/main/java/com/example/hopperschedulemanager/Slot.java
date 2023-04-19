package com.example.hopperschedulemanager;

import java.time.LocalTime;

public class Slot {
    private int id;
    private String dayOfWeek;
    private Boolean openingSlot;
    private Boolean closingSlot;
    private LocalTime startTime;
    private LocalTime endTime;

    public Slot() {
        this.openingSlot = false;
        this.closingSlot = false;
    }

    // ID get set
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Day of week get set
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    //
    public Boolean isOpeningSlot() {
        return openingSlot;
    }

    public void setOpeningSlot(Boolean openingSlot) {
        this.openingSlot = openingSlot;
    }

    public Boolean isClosingSlot() {
        return closingSlot;
    }

    public void setClosingSlot(Boolean closingSlot) {
        this.closingSlot = closingSlot;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
