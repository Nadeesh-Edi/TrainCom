package com.example.traincom.models;

public class Schedule {
    private String id, trainName, date, startTime, pax;

    public Schedule(String id, String trainName, String date, String startTime, String pax) {
        this.id = id;
        this.trainName = trainName;
        this.date = date;
        this.startTime = startTime;
        this.pax = pax;
    }

    public String getId() {
        return id;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getPax() {
        return pax;
    }
}
