import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class FileClient {
    private static String handle;
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static void main(String[] args) {
        handle = "Anonymous"; // Default handle

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();

            try {
                handleCommand(command);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleCommand(String command) throws IOException, ClassNotFoundException {
        String[] parts = command.split(" ");
        String action = parts[0];

        switch (action) {
            case "/join":
                if(parts.length>=3){
                    if (socket != null && !socket.isClosed()) {
                        System.out.println("You are already connected. Please /leave first.");
                        return;
                    }

                    String serverAddress = parts[1];
                    int port = Integer.parseInt(parts[2]);
                    joinServer(serverAddress, port);
                }else{
                    System.out.println("=== Incomplete parameters ===");
                }
                break;

            case "/leave":
                leaveServer();
                break;

            case "/register":
                if (socket != null && !socket.isClosed()){
                    if(parts.length >= 2){
                        String newHandle = parts[1];
                        register(newHandle);
                    }else{
                        System.out.println("=== Please enter a name ===");
                    }
                }else{
                    System.out.println("You are not connected. Please /join first.");
                }
                
                break;

            case "/get":
                if(parts.length>=2){
                String requestedFile = parts[1];
                getFile(requestedFile);
                }else{
                    System.out.println("=== Please enter a Filename ===");
                }
                break;

            case "/store":
                if(parts.length>=2){
                String fileName = parts[1];
                storeFile(fileName);
                }else{
                    System.out.println("=== Please enter a Filename ===");
                }
                break;

            case "/dir":
                listFiles();
                break;
            
            case "/?":
                help();
            break;

            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }

    private static void joinServer(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
    
            System.out.println("Connected to server " + serverAddress + ":" + port);
        } catch (UnknownHostException e) {
            System.err.println("Error: Unknown host " + serverAddress);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error: Cannot connect to server " + serverAddress + ":" + port);
            e.printStackTrace();
        }
    }
    

    private static void leaveServer() throws IOException {
        if (socket != null && !socket.isClosed()) {
            out.writeObject("/disconnect");
            out.close();
            in.close();
            socket.close();

            System.out.println("Disconnected from the server.");
        } else {
            System.out.println("You are not connected to any server.");
        }
    }

    private static void register(String newHandle) throws IOException {
        if (socket != null && !socket.isClosed()) {
        handle = newHandle;
        System.out.println("Handle set to: " + handle);
        } else {
            System.out.println("You are not connected to any server.");
        }
    }

    private static void getFile(String fileName) throws IOException, ClassNotFoundException {
        if (out == null) {
            System.out.println("You are not connected to any server. Please use /join command.");
            return;
        }

        out.writeObject("/get");
        out.writeObject(fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Receive and write file data to the local file
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("File received: " + fileName);
        }
    }


    private static void storeFile(String fileName) throws IOException {
        if (out == null) {
            System.out.println("You are not connected to any server. Please use /join command.");
            return;
        }
        File fileToStore = new File(fileName);
        FileClass file = new FileClass(fileToStore, handle);
        out.writeObject("/store");
        out.writeObject(file);
        System.out.println("File stored: " + file);
    }

    private static void listFiles() throws IOException, ClassNotFoundException {
        if (socket != null && !socket.isClosed()) {
            out.writeObject("/dir");
            Object receivedObject = in.readObject();
        
            if (receivedObject instanceof List) {
                List<?> fileList = (List<?>) receivedObject;
        
                System.out.println("Files in the server:");
                for (Object obj : fileList) {
                    if (obj instanceof FileClass) {
                        FileClass file = (FileClass) obj;
                        System.out.println(file.toString());
                    }
                }
            } else {
                System.out.println("Server returned an unexpected response.");
            }
        } else {
            System.out.println("You are not connected to any server.");
        }
    }


    private static void help(){
        System.out.println("Available commands:");
        System.out.println("/join <server_ip_add> <port>    - Connect to the server application");
        System.out.println("/leave                           - Disconnect from the server application");
        System.out.println("/register <handle>               - Register a unique handle or alias");
        System.out.println("/store <filename>                - Send file to server");
        System.out.println("/dir                             - Request directory file list from a server");
        System.out.println("/get <filename>                  - Fetch a file from a server");
        System.out.println("/?                               - Request command help to output all Input Syntax commands for references");

    }
    
}

