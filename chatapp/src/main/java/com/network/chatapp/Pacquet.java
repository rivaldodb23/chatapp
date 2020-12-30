/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.network.chatapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author rivaldo
 */
public class Pacquet implements Serializable {
    static final int MESSAGE =  0;          //  packet type is set to 0 if packet is a message
    static final int SERVER_INFO =  1;      //  packet type is set to 1 if packet is server information
    int packetType = -1;        //  The type of the packet
    String message;             //  The String content sent by the user
    String fromNickname;        //  The user who sent the message
    ArrayList<String> users;    //  The list of online users

    public Pacquet(String message, String fromNickname, ArrayList<String> users) {
        this.message = message;
        this.fromNickname = fromNickname;
        this.users = users;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Pacquet{" + "packetType=" + packetType + ", message=" + message + ", fromNickname=" + fromNickname + ", users=" + users + '}';
    }
    
    
}
