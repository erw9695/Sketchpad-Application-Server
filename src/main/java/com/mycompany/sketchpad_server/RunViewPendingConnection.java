package com.mycompany.sketchpad_server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ethan Wong
 * Spring 2023
 * RunViewPendingConnection.java
 */
public class RunViewPendingConnection extends Thread {
    @Override
    public void run() {
        try {
            // Create a new server socket and accept new requests to view shared drawings.
            ServerSocket serverSock = new ServerSocket(3000);
            while (true) {
                // Accept new connections.
                Socket viewSocket = serverSock.accept();
                // Handle connections.
                (new ViewPendingConnection(viewSocket)).start();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
