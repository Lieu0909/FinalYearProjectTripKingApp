package com.example.trip.ui.Group;

public class ChatDetails {

    private String messages;
    private String inputType;
    private String senderId;
    private String formID;
    private String timeCreated;
    private String questions;
    private String selection1;
    private String selection2;
    private String selection3;
    private String selection4;

    public ChatDetails() { }
    public ChatDetails(String messages, String inputType, String senderId, String timeCreated) {
        this.messages = messages;
        this.inputType = inputType;
        this.senderId = senderId;
        this.timeCreated = timeCreated;
    }
    public ChatDetails(String inputType, String senderId, String formID, String timeCreated, String questions, String selection1,
                       String selection2, String selection3, String selection4) {
        this.inputType = inputType;
        this.senderId = senderId;
        this.formID = formID;
        this.timeCreated = timeCreated;
        this.questions = questions;
        this.selection1 = selection1;
        this.selection2 = selection2;
        this.selection3 = selection3;
        this.selection4 = selection4;
    }
    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTime() {
        return timeCreated;
    }

    public void setTime(String timeCreated) {
        this.timeCreated = timeCreated;
    }
    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getSelection1() {
        return selection1;
    }

    public void setSelection1(String selection1) {
        this.selection1 = selection1;
    }

    public String getSelection2() {
        return selection2;
    }

    public void setSelection2(String selection2) {
        this.selection2 = selection2;
    }

    public String getSelection3() {
        return selection3;
    }

    public void setSelection3(String selection3) {
        this.selection3 = selection3;
    }

    public String getSelection4() {
        return selection4;
    }

    public void setSelection4(String selection4) {
        this.selection4 = selection4;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }





}
