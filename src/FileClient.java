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
                if (socket != null && !socket.isClosed()) {
                    System.out.println("You are already connected. Please /leave first.");
                    return;
                }

                String serverAddress = parts[1];
                int port = Integer.parseInt(parts[2]);
                joinServer(serverAddress, port);
                break;

            case "/leave":
                leaveServer();
                break;

            case "/register":
                String newHandle = parts[1];
                register(newHandle);
                break;

            case "/get":
                String requestedFile = parts[1];
                getFile(requestedFile);
                break;

            case "/store":
                String fileName = parts[1];
                storeFile(fileName);
                break;

            case "/dir":
                listFiles();
                break;

            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }

    private static void joinServer(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        System.out.println("Connected to server " + serverAddress + ":" + port);
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
        handle = newHandle;
        System.out.println("Handle set to: " + handle);
    }

    private static void getFile(String fileName) throws IOException, ClassNotFoundException {
        if (out == null) {
            System.out.println("You are not connected to any server. Please use /join command.");
            return;
        }

        out.writeObject("/get");
        out.writeObject(fileName);

        File receivedFile = (File) in.readObject();
        if (receivedFile != null) {
            System.out.println("Received file: " + receivedFile);
        } else {
            System.out.println("File not found on the server.");
        }
    }

    private static void storeFile(String fileName) throws IOException {
        if (out == null) {
            System.out.println("You are not connected to any server. Please use /join command.");
            return;
        }

        File file = new File(fileName, handle);
        out.writeObject("/store");
        out.writeObject(file);
        System.out.println("File stored: " + file);
    }

    private static void listFiles() throws IOException, ClassNotFoundException {
        out.writeObject("/dir");
        List<File> files = (List<File>) in.readObject();

        System.out.println("Files in the server:");
        for (File file : files) {
            System.out.println(file);
        }
    }
}

