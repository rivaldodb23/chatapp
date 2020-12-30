/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.network.chatapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rivaldo
 */
public class Server extends Thread {

    int port;
    boolean serverOnline = true;
    ServerSocket serverSocket;
    HashMap<String, ObjectOutputStream> clientOutput = new HashMap<>();
    ArrayList<String> users = new ArrayList<>();
    ArrayList<ClientHandler> clients = new ArrayList<>();
    
    public Server(int port) {
        try {
            this.port = port;
            serverSocket = new ServerSocket(port);
            System.out.println("Server socket made");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean hasName(String name) {
        return clientOutput.containsKey(name);
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public ArrayList<ClientHandler> getClients() {
        return clients;
    }

    /**
     * Establish connection with client Broadcast to all users that a new one
     * has come Send an updated version of the user list
     */
    @Override
    public void run() {
        while (serverOnline) {
            try {
                //  Accept a server connection from a client
                Socket socket = serverSocket.accept();
                System.out.println("Socket connection made");

                //  Get the input and output streams
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                //  Read in the nickname. Add random numbers until the nickname is unique
                String nickname = "";
                nickname = ((Pacquet) in.readObject()).message;
                System.out.println("Nickname received " + nickname);
                while (true) {
                    if (hasName(nickname)) {
                        int random = (int) (Math.random() * 10);
                        nickname = nickname + "" + random;
                    } else {
                        users.add(nickname);
                        break;
                    }
                    
                    System.out.println("Users List " + users);
                }

                //  Create a ClientHandler thread and start it
                ClientHandler client = new ClientHandler(nickname, socket, out, in);
                client.start();
                
                

                //  Wait until client is online
                while (!client.online) //  Add nickname as key to clientOutput hashmap with the outputstream as value
                {
                    clientOutput.put(nickname, client.out);
                }
                System.out.println("Nickname added to list");

                //  Compose a new packet as server and broadcast to all users that a new member has come
                Pacquet p = new Pacquet(nickname, "Server", users);
                p.packetType = Pacquet.MESSAGE;
                out.writeObject(p);
                out.flush();
                System.out.println("Nickname sent " + nickname);
                for (String user : users) {
                    ObjectOutputStream userOut = clientOutput.get(user);
                    Pacquet packet = new Pacquet("Welcome to " + nickname, "Server", users);
                    userOut.writeObject(packet);
                    userOut.flush();
                    
                }
                ArrayList<String> usersCopy = new ArrayList<>();
                for (String user : users) {
                    usersCopy.add(user);
                }
                for (String user : users) {
                    System.out.println("Users = " + users + " sent to " + user);
                    ObjectOutputStream userOut = clientOutput.get(user);
                    Pacquet packet = new Pacquet("Server update: ", "Server", usersCopy);
                    packet.packetType = Pacquet.SERVER_INFO;
                    userOut.writeObject(packet);
                    userOut.flush();
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class ClientHandler extends Thread {

        String nickname;
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        boolean online = false;

        public ClientHandler(String nickname, Socket socket, ObjectOutputStream out, ObjectInputStream in) {
            this.nickname = nickname;
            this.socket = socket;
            this.out = out;
            this.in = in;
        }

        /**
         * Receives a new packet in input stream Broadcast the packet to
         * destination(s)
         */
        @Override
        public void run() {
            online = true;
            while (online) {
                broadCastPacket();
            }
        }

        private void broadCastPacket() {
            try {
                Pacquet packet = (Pacquet) in.readObject();
                ArrayList<String> usersCopy = new ArrayList<>();
                for (String user : users) {
                    usersCopy.add(user);
                }
                packet.setUsers(usersCopy);
                System.out.println("Packet at server: \t" + packet.users);
                packet.message = packet.message + "Server  mod";
                packet.packetType = Pacquet.MESSAGE;
                for (String user : users) {
                    System.out.println("Sending to " + user);
                    out = clientOutput.get(user);
                    out.writeObject(packet);
                    out.flush();
                }
            } catch (IOException | ClassNotFoundException ex) {
                try {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    online = false;
                    users.remove(nickname);
                    socket.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }
}
