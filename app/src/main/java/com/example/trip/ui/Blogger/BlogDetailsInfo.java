package com.example.trip.ui.Blogger;

public class BlogDetailsInfo {

    public String blogID;
    public String destinationPic;
    public String destinationName;
    public String destinationDescription;
    public Float destinationBudget;

    public BlogDetailsInfo() {
    }

    public BlogDetailsInfo(String blogID, String destinationName, String destinationDescription, Float destinationBudget, String destinationPic) {
        this.blogID=blogID;
        this.destinationName = destinationName;
        this.destinationDescription = destinationDescription;
        this.destinationBudget = destinationBudget;
        this.destinationPic = destinationPic;
    }

    public String getBlogID() {
        return blogID;
    }

    public void setBlogID(String blogID) {
        this.blogID = blogID;
    }
    public String getDestinationPic() {
        return destinationPic;
    }

    public void setDestinationPic(String destinationPic) {
        this.destinationPic = destinationPic;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationDescription() {
        return destinationDescription;
    }

    public void setDestinationDescription(String destinationDescription) {
        this.destinationDescription = destinationDescription;
    }

    public Float getDestinationBudget() {
        return destinationBudget;
    }

    public void setDestinationBudget(Float destinationBudget) {
        this.destinationBudget = destinationBudget;
    }






}
