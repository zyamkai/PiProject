package com.example.callapp.Models;

public class ConferenceRoom {

    private User user = null;
    private String conferenceRoomId;
    private String ownerId;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ConferenceRoom(String conferenceRoomId, User user) {
        this.user = user;
        this.conferenceRoomId = conferenceRoomId;
    }

    public ConferenceRoom(){

    }


    public String getConferenceRoomId() {
        return conferenceRoomId;
    }

    public void setConferenceRoomId(String conferenceRoomId) {
        this.conferenceRoomId = conferenceRoomId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
