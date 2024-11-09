package com.example.trip.ui.Blogger;

public class CreateBlogInfo {

    public String tripName;
    public String dayStart;
    public String dayEnd;
    public String imageURL;
    public String date;
    public String blogID;
    public String status;
    public String shareTime;
    public String description;
    public Integer dayNo;
    public String uID;
    public CreateBlogInfo(){};

    public CreateBlogInfo(String blogID,String tripName, String dayStart, String dayEnd, String imageURL, String date, String uID,String status) {
        this.blogID=blogID;
        this.tripName = tripName;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.imageURL = imageURL;
        this.date=date;
        this.uID = uID;
        this.status=status;
    }
    public CreateBlogInfo(String uID, String tripName, String dayStart, String dayEnd, String date) {
        this.uID=uID;
        this.tripName = tripName;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.date=date;

    }

    public CreateBlogInfo(String uID, String tripName, String description, String dayStart, String dayEnd, String date, Integer dayNo ,String status) {
        this.uID=uID;
        this.tripName = tripName;
        this.description=description;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.date=date;
        this.dayNo=dayNo;
        this.status=status;
    }

    public Integer getDayNo() {
        return dayNo;
    }

    public void setDayNo(Integer dayNo) {
        this.dayNo = dayNo;
    }

    public String getBlogID() {
        return blogID;
    }

    public void setBlogID(String blogID) {
        this.blogID = blogID;
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
    public String getShareTime() {
        return shareTime;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShareTime(String shareTime) {
        this.shareTime = shareTime;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getDayStart() {
        return dayStart;
    }

    public void setDayStart(String dayStart) {
        this.dayStart = dayStart;
    }

    public String getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(String dayEnd) {
        this.dayEnd = dayEnd;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
