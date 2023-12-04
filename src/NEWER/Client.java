package NEWER;

import java.io.*;
import java.net.*;
import java.util.*; // For Scanner


public class Client {
    private static final String COMMAND_PREFIX = "/";
    public static void main(String[] args) {
        String host = "localhost"; // can be changed
        int port = 5000;

        Scanner sc = new Scanner(System.in);
        connectToServer(host, port, sc);

        String msg;
        try {
            Socket endpoint = new Socket(host, port);

            System.out.println("Client: Has connected to server " + host + ":" + port);

            DataInputStream reader = new DataInputStream(endpoint.getInputStream());
            DataOutputStream writer = new DataOutputStream(endpoint.getOutputStream());

            System.out.print("> ");
            // Let's try inputting a string in the console
            while (!(msg = sc.nextLine()).equals("END")) {
                // The message will be send to the server
                writer.writeUTF(msg);
                // The Server will append "Server: " so that
                // we know that the message really was accepted
                // by the server
                System.out.println(reader.readUTF());
                System.out.print("> ");
            }

            // Send the terminal String to the Server
            writer.writeUTF("END");

            System.out.println("Client: has terminated connection");
            sc.close();
            endpoint.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void connectToServer(String host, int port, Scanner sc) {
        Socket endpoint;
        try {
            endpoint = new Socket(host, port);

            System.out.println("Client: Has connected to server " + host + ":" + port);

            // ... (existing code)

            // Handle user commands
            handleCommands(endpoint, sc);

            endpoint.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        private static void handleCommands (Socket endpoint, Scanner sc){
            try {
                DataInputStream reader = new DataInputStream(endpoint.getInputStream());
                DataOutputStream writer = new DataOutputStream(endpoint.getOutputStream());

                while (true) {
                    System.out.print("> ");
                    String input = sc.nextLine();

                    if (input.startsWith(COMMAND_PREFIX)) {
                        handleCommand(input, writer, reader);
                    } else {
                        writer.writeUTF(input); // Send regular message to the server
                        System.out.println(reader.readUTF());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static void handleCommand (String input, DataOutputStream writer, DataInputStream reader) throws
        IOException {
            String[] commandParts = input.split(" ");
            String command = commandParts[0].substring(1); // Remove the leading "/"

            switch (command) {
                case "leave":
                    writer.writeUTF("END");
                    System.out.println(reader.readUTF());
                    System.out.println("Client: has terminated connection");
                    System.exit(0); // Exit the client
                    break;
                case "register":
                    // Handle user registration, if needed
                    break;
                case "store":
                    // Handle file storing
                    sendFileToServer(commandParts[1], writer, reader);
                    break;
                case "dir":
                    // Request directory file list
                    writer.writeUTF(command);
                    System.out.println(reader.readUTF());
                    break;
                case "get":
                    // Fetch a file from the server
                    writer.writeUTF(command + " " + commandParts[1]);
                    System.out.println(reader.readUTF());
                    break;
                case "?":
                    // Display command help
                    displayCommandHelp();
                    break;
                default:
                    System.out.println("Unknown command. Type '/?' for help.");
            }
        }

        private static void sendFileToServer (String filename, DataOutputStream writer, DataInputStream reader) throws
        IOException {
            // Implement file sending logic here
            // You can use the FileClass or standard Java File and FileInputStream to send the file
        }

        private static void displayCommandHelp () {
            System.out.println("Available commands:");
            System.out.println("/leave - Disconnect from the server");
            System.out.println("/register <handle> - Register a unique handle or alias");
            System.out.println("/store <filename> - Send file to server");
            System.out.println("/dir - Request directory file list from the server");
            System.out.println("/get <filename> - Fetch a file from the server");
            System.out.println("/? - Request command help");
        }

    }