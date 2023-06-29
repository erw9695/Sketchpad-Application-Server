package com.mycompany.sketchpad_server;

import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Ethan Wong
 * Spring 2023
 * AcceptShareConnection.java
 */
public class AcceptShareConnection extends Thread {
    Socket sock;
    Scanner sIn;
    PrintStream sOut;
    
    AcceptShareConnection(Socket drawingSock) {
        sock = drawingSock;
    }
    
    @Override
    public void run() {
        // SQL Declarations
        Connection conn = null;
        Statement saveDrawState = null;
        Statement saveShapeState = null;        
        
        try {
            // Create scanner and print stream.
            sIn = new Scanner(sock.getInputStream());
            sOut = new PrintStream(sock.getOutputStream());
            
            // Get the first string, which will be the sender, recipient, and drawing name.
            String newDrawing = sIn.next();
            
            // Connect to server database.
            String url = "jdbc:mariadb://localhost:3306/drawdbserver";
            conn = DriverManager.getConnection(url,"","");
            
            // Add drawing to server_drawings table.
            String saveDrawing = "insert into server_drawings values "+newDrawing;
            saveDrawState = conn.createStatement();
            saveDrawState.execute(saveDrawing);
            
            saveShapeState = conn.createStatement();
            
            // Loop to receive all shapes sent.
            while (sIn.hasNext()) {
                // newShape will be pre-formatted for the insert query.
                String newShape = sIn.next();
                String saveShape = "insert into server_shapes values "+newShape;
                saveShapeState.execute(saveShape);
            }
            
            // Send the status back to the client.
            sOut.println("Status: Successfully shared drawing.");
            sock.close();
        } catch (Exception e) {
            System.out.println("AcceptShareConnection:"+e.toString());
        } finally {
            // Close SQL connection.
            if (saveDrawState != null) try { saveDrawState.close(); } catch (Exception e2) {}
            if (saveShapeState != null) try { saveShapeState.close(); } catch (Exception e3) {}
            if (conn != null) try { conn.close(); } catch (Exception e4) {}
        }
    }
}
