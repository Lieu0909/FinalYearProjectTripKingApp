package com.example.trip.ui.Group;

public class GroupDetails {

    private String uId;
    private String groupName;
    private String groupPic;
    private String groupID;
    private String timestamp;
    private String createBy;
    public GroupDetails() {}

    public GroupDetails(String groupName, String groupPic,String createBy,String uId,String groupID) {
        this.groupName = groupName;
        this.groupPic = groupPic;
        this.createBy=createBy;
        this.uId=uId;
        this.groupID=groupID;
    }
    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPic() {
        return groupPic;
    }

    public void setGroupPic(String groupPic) {
        this.groupPic = groupPic;
    }

}
