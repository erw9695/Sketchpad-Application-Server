package com.mycompany.sketchpad_server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ethan Wong
 * Spring 2023
 * RunDownloadReqConnection.java
 */
public class RunDownloadReqConnection extends Thread {
    @Override
    public void run() {
        try {
            // Create a new server socket and accept new download requests.
            ServerSocket serverSock = new ServerSocket(4000);
            while (true) {
                // Accept new connections.
                Socket downloadSocket = serverSock.accept();
                // Handle connections.
                (new DownloadReqConnection(downloadSocket)).start();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
