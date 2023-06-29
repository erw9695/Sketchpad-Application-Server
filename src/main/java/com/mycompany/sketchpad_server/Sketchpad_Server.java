package com.mycompany.sketchpad_server;

/**
 * Ethan Wong
 Spring 2023
 Sketchpad_Server.java
 */
public class Sketchpad_Server {
    public static void main(String[] args) {
        System.out.println("SERVER - RUNNING");
        
        // When the server starts, start the following four threads to simultaneously handle authentication, share drawing actions,
        // view shared drawing actions, and download/deletion requests.
        (new RunClientConnection()).start();
        (new RunAcceptShareConnection()).start();
        (new RunViewPendingConnection()).start();
        (new RunDownloadReqConnection()).start();
    }
}
