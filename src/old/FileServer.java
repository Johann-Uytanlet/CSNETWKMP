package old;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FileServer {
    private static List<FileClass> files = new ArrayList<>();

    public static void main(String[] args) {
        int port = 12345; // Change this to the desired port

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("File Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Handle client in a new thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            while (true) {
                String command = (String) in.readObject();

                switch (command) {
                    case "/store":
                        FileClass file = (FileClass) in.readObject();
                        System.out.println(file);
                        files.add(file);
                        System.out.println("File stored: " + file);
                        break;

                    case "/dir":
                        out.writeObject(files);
                        break;

                    case "/get":
                        String fileName = (String) in.readObject();
                        sendFile(out, fileName);
                        break;

                    case "/disconnect":
                        System.out.println("Client disconnected: " + clientSocket);
                        return;

                    default:
                        System.out.println("Unknown command: " + command);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(ObjectOutputStream out, String fileName) throws IOException {
        for (FileClass file : files) {
            if (file.getFile().getName().equals(fileName)) {
                try (FileInputStream fileInputStream = new FileInputStream(file.getFile())) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    // Send the file data in chunks
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }

                    // Signal the end of file transfer
                    out.writeObject(null);

                    System.out.println("File sent: " + file);
                    return;
                }
            }
        }

        // If the file is not found, inform the client
        out.writeObject(null);
    }
}
