package NEWER;

import java.io.*;
import java.net.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Connection extends Thread {
    private Socket socket;

    public Connection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String msg;
            DataInputStream reader = new DataInputStream(socket.getInputStream());
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

            // This checks whether the string that was sent from
            // the client side is the terminal "END" else we
            // send the string back to the client
            while (!(msg = reader.readUTF()).equals("END")) {
                writer.writeUTF("Server: " + msg);
            }

            socket.close();
        } catch (Exception e) {
            // e.printStackTrace(); // Uncomment this if you want to look at the error thrown
        } finally {
            System.out.println("Server: Client " + socket.getRemoteSocketAddress() + " has disconnected");
        }
    }
}
