import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class ClientThread {
    static final int PORT = 16000;

    public static void main(String args[]) {
        //InputStream in = null;
        BufferedReader br = null;
        PrintWriter out = null;
        InputStream in = null;
        Socket socket = null;
        Scanner sc = new Scanner(System.in);
        
        try {
            socket = new Socket("localhost", 16000);
            in = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return;
        }
        String line;
        String data;
        boolean connected = true;
        while (connected) {
            try {
                data = sc.next();
                out.println(data);
                out.flush();

                line = br.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    connected = false;
                    return;
                } else {
                    System.out.println(line);
                }
            
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
                return;
            }
        }
        try{
            out.close();
            br.close();
            socket.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}