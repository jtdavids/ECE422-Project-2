
import java.io.*;
import java.net.*;


public class ThreadedServer {

    static final int PORT = 16000;

    public static void main(String args[]) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        int client_count = 0;
        System.out.println("Server running...");
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                socket = serverSocket.accept();
                client_count++;
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new threa for a client
            new ClientHandler(socket, client_count).start();
        }
    }
}