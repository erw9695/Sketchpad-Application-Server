package com.mycompany.sketchpad_server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ethan Wong
 * Spring 2023
 * RunClientConnection.java
 */
public class RunClientConnection extends Thread {
    @Override
    public void run() {
        try {
            // Create a new server socket and accept new authentication requests from clients.
            ServerSocket serverSock = new ServerSocket(5190);
            while (true) {
                // Accept new authentication requests.
                Socket clientSock = serverSock.accept();
                // Create ClientConnection object to handle request.
                (new ClientConnection(clientSock)).start();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
