package com.example.geofencingmajorproject;

public class ConnectedUsers {
    String request;
    String email;

    public ConnectedUsers(String request, String email){
        this.email = email;
        this.request = request;
    }

    public String getConnectedEmail() {return email;}

    public void setConnectedEmail(String email) {this.email = email;}

    public String getConnectedRequest() {return request;}

    public void setConnectedRequest(String request) {this.request = request;}
}
