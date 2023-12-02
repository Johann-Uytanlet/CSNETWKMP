import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FileServer {
    private static List<File> files = new ArrayList<>();

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
                    // ... (existing cases remain the same)

                    case "/get":
                        String fileName = (String) in.readObject();
                        sendFile(out, fileName);
                        break;

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
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                out.writeObject(file);
                return;
            }
        }

        // If the file is not found, inform the client
        out.writeObject(null);
    }
}
