package com.mycompany.sketchpad_server;

import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Ethan Wong
 * Spring 2023
 * ViewPendingConnection.java
 */
public class ViewPendingConnection extends Thread {
    Socket sock;
    Scanner sIn;
    PrintStream sOut;
    String recipientName;
    
    ViewPendingConnection(Socket downloadSock) {
        sock = downloadSock;
    }
    
    @Override
    public void run() {
        try {
            // Create scanner and print stream.
            sIn = new Scanner(sock.getInputStream());
            sOut = new PrintStream(sock.getOutputStream());
            
            // Get the recipient's username.
            recipientName = sIn.next();
            
            // SQL Declaration
            Connection conn = null;
            Statement recipState = null;
            ResultSet drawingsFound = null;
            
            try {
                // Connect to user database.
                String url = "jdbc:mariadb://localhost:3306/drawdbserver";
                conn = DriverManager.getConnection(url,"","");
                
                // Get all drawings sent to the recipient.
                String recipientDraw = "select * from server_drawings where recipient = '"+recipientName+"'";
                recipState = conn.createStatement();
                drawingsFound = recipState.executeQuery(recipientDraw);
                
                // Send all pending drawings to the recipient.
                while (drawingsFound.next()) {
                    String senderVal = drawingsFound.getString("sender");
                    String drawingNameVal = drawingsFound.getString("drawingName");
                    
                    // Send the drawing name and then the sender's name.
                    sOut.println(drawingNameVal);
                    sOut.println(senderVal);
                }
                
            } catch (Exception ex) {
                System.out.println(ex.toString());
            } finally {
                // Close SQL connection.
                if (drawingsFound != null) try { drawingsFound.close(); } catch (Exception e2) {}
                if (recipState != null) try { recipState.close(); } catch (Exception e3) {}
                if (conn != null) try { conn.close(); } catch (Exception e4) {}
            }
            
            sock.close();
            
        } catch (Exception e) {
            System.out.println("AcceptShareConnection:"+e.toString());
        }
    }
}
