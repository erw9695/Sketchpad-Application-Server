package com.mycompany.sketchpad_server;

import java.io.PrintStream;
import java.net.Socket;
import java.sql.*;
import java.util.Scanner;

/**
 * Ethan Wong
 * Spring 2023
 * DownloadReqConnecton.java
 */
public class DownloadReqConnection extends Thread {
    Socket sock;
    Scanner sIn;
    PrintStream sOut;
    String recipientName;
    
    DownloadReqConnection(Socket downloadSock) {
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
            Statement delDrawState = null;
            Statement delShapeState = null;
            Statement getState = null;
            ResultSet shapesFound = null;
        
            try {
                // Connect to user database.
                String url = "jdbc:mariadb://localhost:3306/drawdbserver";
                conn = DriverManager.getConnection(url,"","");
                
                // Get download request from the recipient.
                String specifiedDrawing = sIn.next();
                String specifiedSender = sIn.next();
                String requestType = sIn.next();

                // Prepare queries to remove drawing from the server database once it is successfully sent to the recipient or to fulfill a delete request.
                String deleteDrawing = "delete from server_drawings where sender = '"+specifiedSender+"' and recipient = '"+recipientName+"' and drawingName = '"+specifiedDrawing+"'";
                String deleteShapes = "delete from server_shapes where sender = '"+specifiedSender+"' and recipient = '"+recipientName+"' and drawingName = '"+specifiedDrawing+"'";
                delDrawState = conn.createStatement();
                delShapeState = conn.createStatement();

                // If the user wants to download this drawing ...
                if (requestType.equals("download")) {
                    // Get all shapes associated with this drawing.
                    String getShapes = "select * from server_shapes where sender = '"+specifiedSender+"' and drawingName = '"+specifiedDrawing+"' and recipient = '"+recipientName+"'";
                    getState = conn.createStatement();
                    shapesFound = getState.executeQuery(getShapes);

                    // Send all of the shapes to the recipient, formatted for insertion into their local shapes database.
                    while (shapesFound.next()) {
                        String shapeId = shapesFound.getString("id");
                        String shapeType = shapesFound.getString("type");
                        int shapexCoord = shapesFound.getInt("xCoord");
                        int shapeyCoord = shapesFound.getInt("yCoord");
                        int shapeSize = shapesFound.getInt("size");
                        String shapeColor = shapesFound.getString("color");

                        sOut.println("('"+shapeId+"','"+recipientName+"','"+specifiedDrawing+"','"+shapeType+"',"+shapexCoord+","+shapeyCoord+","+shapeSize+",'"+shapeColor+"')");
                    }
                } 

                // Remove the drawing from the server's database.
                delDrawState.execute(deleteDrawing);
                delShapeState.execute(deleteShapes);
                
            } catch (Exception ex) {
                System.out.println(ex.toString());
            } finally {
                // Close SQL connection.
                if (shapesFound != null) try { shapesFound.close(); } catch (Exception e2) {}
                if (delDrawState != null) try { delDrawState.close(); } catch (Exception e3) {}
                if (delShapeState != null) try { delShapeState.close(); } catch (Exception e4) {}
                if (getState != null) try { getState.close(); } catch (Exception e4) {}
                if (conn != null) try { conn.close(); } catch (Exception e5) {}
            }
            
            sock.close();
        } catch (Exception e) {
            System.out.println("AcceptShareConnection:"+e.toString());
        }
    }
}
