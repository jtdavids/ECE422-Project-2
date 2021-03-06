import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.ByteBuffer;

public class ClientThread {
    static final int PORT = 16000;
    

    public static void main(String args[]) {
        //InputStream in = null;
        BufferedReader br = null;
        PrintWriter out = null;
        InputStream in = null;
        Socket socket = null;
        InputStreamReader user_in = new InputStreamReader(System.in);
        BufferedReader user_br = new BufferedReader(user_in);
        byte[] pub_key;
        byte[] pri_key;
        Encrypter en = new Encrypter();
        
        // KEY GENERATION TESTING -------------------------------
        //try{
            /*SecureRandom random = new SecureRandom();
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("DH");
            keygen.initialize(1024, random);
            KeyPair keys = keygen.generateKeyPair();
            pub_key = keys.getPublic().getEncoded();
            pri_key = keys.getPrivate().getEncoded();
            System.out.println("Public key: " + pub_key);
            System.out.println("Private key: " + pri_key);

            */
            // KEY GENERATION TESTING --------------------------------
            // ENCRYPTION TESTING ----------------------------
            /*
            String str = "Hello World!!!!!AAABBBAAABBBAAAB1234567812345678NNNNNNNNNNNNNNNNHello World!!!!!Hello World!!!!!";
            byte[] val = str.getBytes(StandardCharsets.UTF_8);
            System.out.println("Text in Bytes : " + val);

            long[] key = { 78945677, 87678687, 234234, 234234 };

            val = en.encrypt(val, key);
            System.out.println("encryption complete...");

            String s1 = new String(val, StandardCharsets.UTF_8);
            System.out.println("Text Encryted : " + s1);
            System.out.println("decrypting...");
            val = en.decrypt(val, key);
            String s2 = new String(val, StandardCharsets.UTF_8);
            System.out.println("Text Decryted : " + s2);
            */
            // ENCRYPTION TESTING ----------------------------
       // } catch (NoSuchAlgorithmException e){
         //   e.printStackTrace();
           // return;
        //}
        try {
            socket = new Socket("localhost", 16000);
            in = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return;
        }
        String line;
        String input;
        String filename = "";
        boolean connected = true;
        int size = 0;

        System.out.println("For authentication enter 'AUTHUSER <username> <password>");
        System.out.println("For adding new users enter 'ADDUSER <username> <password>");
        System.out.println("For file transfer enter 'F <filepath> <filename>'");
        System.out.println("For logoff enter 'finished'");


        while (connected) {
            try {

                input = user_br.readLine();
                if(input.split(" ")[0].equals("F")){
                    filename = input.split(" ")[2];
                }
                en.sendMessage(socket, input);

                line = en.readMessage(socket);

                if (line == null || line.equals("null")) {
                    connected = false;
                    return;
                } else {
                    switch(line){
                        case "VAL":
                            System.out.println("Access Granted");
                            break;
                        case "INV":
                            System.out.println("Access Denied");
                            break;
                        case "NEW":
                            System.out.println("User added");
                            break;
                        case "ACK":
                            System.out.println("Transferring file...");
                            Path out_path = Paths.get(filename);
                            System.out.println("filename: " + filename);
                            byte[] file = en.recieveFile(socket);
                            System.out.println("recieved file: " + file);
                            Files.write(out_path, file);
                            System.out.println("Transfer Complete!");
                            break;
                        case "FNF":
                            System.out.println("Error: File not found.");
                            break;
                        case "QUIT":
                            connected = false;
                            break;
                        case "URC":
                            System.out.println("Unrecognized Command");
                            break;
                        default:
                            System.out.println(line);
                            break;
                    }
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