package com.example.trip.ui.MyTrip;

public class Trip {


    String tripName;
    String tripSDate;
    String tripEDate;
    Integer tripNoDay;
    String tripId;
    String uID;

    public Trip(){}
    public Trip(String tripName, String tripSDate, String tripEDate, Integer tripNoDay, String tripId,String uID) {
        this.tripName = tripName;
        this.tripSDate = tripSDate;
        this.tripEDate = tripEDate;
        this.tripNoDay = tripNoDay;
        this.tripId = tripId;
        this.uID=uID;
    }
    public Trip(String uID,String tripName, String tripSDate, String tripEDate,Integer tripNoDay) {
        this.uID = uID;
        this.tripName = tripName;
        this.tripSDate = tripSDate;
        this.tripEDate = tripEDate;
        this.tripNoDay = tripNoDay;

    }
    public Trip(String uID,String tripName, Integer tripNoDay) {
        this.uID = uID;
        this.tripName = tripName;
        this.tripNoDay = tripNoDay;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripSDate() {
        return tripSDate;
    }

    public void setTripSDate(String tripSDate) {
        this.tripSDate = tripSDate;
    }

    public String getTripEDate() {
        return tripEDate;
    }

    public void setTripEDate(String tripEDate) {
        this.tripEDate = tripEDate;
    }

    public Integer getTripNoDay() {
        return tripNoDay;
    }

    public void setTripNoDay(Integer tripNoDay) {
        this.tripNoDay = tripNoDay;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }










}
