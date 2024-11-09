package com.example.trip.ui.Group;

public class Participants {

    String memberID;
    String name;
    String email;
    String role;
    String picture;
    public Participants() { }

    public Participants(String name,String role,String picture) {
        this.name = name;
        this.role=role;
        this.picture=picture;
    }
    public String getRole() {
        return role;
    }
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



}
