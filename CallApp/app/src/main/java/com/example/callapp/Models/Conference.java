package com.example.callapp.Models;

public class Conference {
    public String conferenceId;
    public User user;


    public Conference(User user, String conferenceId){
        this.user = user;
        this.conferenceId = conferenceId;


    }

    public Conference(){}





    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
