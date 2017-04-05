import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    protected Socket socket;
    private int clientID;

    public ClientHandler(Socket clientSocket, int clientID) {
        this.socket = clientSocket;
        this.clientID = clientID;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        PrintWriter out = null;

        System.out.println("Client " + clientID + " Connected...");

        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return;
        }
        
        String line;
        boolean connected = true;
        while (connected) {
            try {
                line = brinp.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    System.out.println("Quit Recieved");
                    out.println("quit");
                    connected = false;
                    return;
                } else {
                    out.println(line.toUpperCase());
                    out.flush();
                }
            } catch (IOException e) {
                connected = false;
                return;
            }
        }
        System.out.println("Client #" + clientID + " disconnected!");
        try{
            out.close();
            brinp.close();
            socket.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}