package com.example.trip.ui.MyTrip;

public class TripDetailsInfo {

    String place;
    String time;
    String tripID;
    String fragment_no;

    public TripDetailsInfo() { }
    public TripDetailsInfo( String tripID,String place,String fragment_no) {
        this.tripID=tripID;
        this.place = place;
        this.fragment_no=fragment_no;
    }
    public TripDetailsInfo(String tripID, String place, String time,String fragment_no) {
        this.tripID=tripID;
        this.place = place;
        this.time = time;
        this.fragment_no=fragment_no;
    }

    public String getFragment_no() {
        return fragment_no;
    }

    public void setFragment_no(String fragment_no) {
        this.fragment_no = fragment_no;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
