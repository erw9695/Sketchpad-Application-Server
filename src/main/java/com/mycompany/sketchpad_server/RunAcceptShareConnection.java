package com.mycompany.sketchpad_server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ethan Wong
 * Spring 2023
 * RunAcceptShareConnection.java
 */
public class RunAcceptShareConnection extends Thread {
    @Override
    public void run() {
        try {
            // Create a new server socket and accept new drawings to be shared.
            ServerSocket serverSock = new ServerSocket(5000);
            while (true) {
                // Accept new connections.
                Socket drawingSock = serverSock.accept();
                // Handle connection.
                (new AcceptShareConnection(drawingSock)).start();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
