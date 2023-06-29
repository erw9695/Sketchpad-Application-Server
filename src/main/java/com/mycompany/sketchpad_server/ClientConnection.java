package com.mycompany.sketchpad_server;

import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;

/**
 * Ethan Wong
 * Spring 2023
 * ClientConnection.java
 */
public class ClientConnection extends Thread {
    Socket sock;
    Scanner sIn;
    PrintStream sOut;
    String username;
    String password;
    Boolean signInAttempt;
    
    ClientConnection(Socket newSock) {
        sock = newSock;
    }
    
    @Override
    public void run() {
        // SQL Declaration
        Connection conn = null;
        Statement authState = null;
        Statement registerState = null;
        ResultSet authResult = null;
        
        
        try {
            // Create scanner and print stream.
            sIn = new Scanner(sock.getInputStream());
            sOut = new PrintStream(sock.getOutputStream());
            
            // Get the username, password, and connection attempt type.
            username = sIn.next();
            password = sIn.next();
            // True if a sign-in attempt, false if its a registration attempt.
            signInAttempt = Boolean.parseBoolean(sIn.next());
            
            // Connect to server database.
            String url = "jdbc:mariadb://localhost:3306/drawdbserver";
            conn = DriverManager.getConnection(url,"","");
            
            // If the client is attempting to sign-in to their account ...
            if (signInAttempt) {
                // Query to get password for the user.
                String authenticate = "select password from users where username = '"+username+"'";
                authState = conn.createStatement();

                // Execute query.
                authResult = authState.executeQuery(authenticate);
                
                // If we have a result (user exists) ...
                if (authResult.next()) {
                    // Get the password returned.
                    String pass = authResult.getString("password");

                    // If the password sent by the client matches the password from the database, authenticate.
                    if (pass.equals(password)) {
                        System.out.println("SERVER: USER AUTHENTICATED"); 
                        sOut.println("SUCCESS");
                    } else { // Otherwise, indicate it failed.
                        System.out.println("SERVER: USER FAILED TO AUTHENTICATE"); 
                        sOut.println("FAILURE");
                    }
                } else { // User doesn't exist, indicate failure.
                    System.out.println("SERVER: USER FAILED TO AUTHENTICATE"); 
                    sOut.println("FAILURE");
                }
            } else { // If this isn't a sign-in attempt, attempt to register the user.
                String registerUser = "insert into users values ('"+username+"','"+password+"');"; 
                registerState = conn.createStatement();
                
                // If the registration attempt succeeds, add it to the DB and indicate this to the client.
                try {
                    registerState.execute(registerUser);
                    System.out.println("SERVER: USER REGISTERED"); 
                    sOut.println("SUCCESS");
                } catch (Exception ex) { // If we get an exception when registering, indicate a failure.
                    System.out.println("SERVER: USER FAILED TO REGISTER");
                    sOut.println("FAILURE");
                }
            }
            
            sock.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            // Close SQL connection.
            if (authResult != null) try { authResult.close(); } catch (Exception e2) {}
            if (authState != null) try { authState.close(); } catch (Exception e3) {}
            if (registerState != null) try { registerState.close(); } catch (Exception e4) {}
            if (conn != null) try { conn.close(); } catch (Exception e5) {}
        }
    }
}
