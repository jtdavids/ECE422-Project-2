import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.math.BigInteger;

public class ClientHandler extends Thread {
    protected Socket socket;
    private int clientID;
    private long[] hashKey = { 789123333, 876999222, 234234432, 234234567 };
    private long[] sharedKey = { 886223543, 114558990, 222323881, 234123879 };

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
        int size = 0;
        boolean connected = true;
        String username;
        String password;
        String filepath;

        while (connected) {
            try {
                size = brinp.readLine();
                byte[] data = new byte[size];
                data = brinp.readLine();
                String[] inputs = line.split(" ");
                if (line == null || line.equals("null")){
                    connected = false;
                    continue;
                } else {
                    
                    switch(inputs[0]){
                        case "AUTHUSER": 
                            username = inputs[1];
                            password = inputs[2];
                            if(authenticate(username, password)){
                                out.println("VAL");
                            }else{
                                out.println("INV");
                            }
                            break;
                        case "ADDUSER":
                            username = inputs[1];
                            password = inputs[2];
                            newUser(username, password);
                            out.println("NEW");
                            break;
                        case "F":
                            filepath = inputs[1];
                            Path path = Paths.get(filepath);
                            if(!Files.exists(path)){
                                out.println("FNF");
                            }else{
                                byte[] file = retrieveFile(path);
                                int size = file.length;
                                out.println("ACK");
                            }
                            
                            //out.println(file);
                            break;
                        
                        case "finished":
                            out.println("QUIT");
                            connected = false;
                            break;
                        default:
                            out.println("URC");
                            break;
                    }
                    
                }
                out.flush();
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
    public boolean authenticate(String user, String password){
        Encrypter en = new Encrypter();
        String line;
        String salt = "";
        String hashed_pw = "";
        boolean authenticated = false;
        boolean user_found = false;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("shadowfile.txt"), "UTF-8"));
            
            //Find User 
            while ((line = br.readLine()) != null && !user_found) {
                if(line.equals(user)){
                    salt = br.readLine();
                    hashed_pw = br.readLine();
                    user_found = true;
                }
            }
            if(!user_found){
                return false;
            }
            //salt = salt.replace("\n", "");
            //hashed_pw.replace("\n", "");
            byte[] salted_pw = saltPassword(password, salt);
            String salt_string = new String(salted_pw, StandardCharsets.UTF_8);
            byte[] computed_hash = en.encrypt(salted_pw, hashKey);
            System.out.println("salted password: " + salt_string);

            String new_pw = new String(computed_hash, StandardCharsets.UTF_8);
            //System.out.println("Text Encryted : " + new_pw);

            System.out.println("HASH COMPARE: " + (hashed_pw.equals(new_pw)));
            authenticated = (hashed_pw.equals(new_pw));
            System.out.println("stored pw length: " + hashed_pw.length());
            //System.out.println("byte pw: " + salted_pw);
            System.out.println("string pw: " + new_pw);
            //System.out.println("string pw (bytes): " + new_pw.getBytes(StandardCharsets.UTF_8));

            //System.out.println("decrypting...");
            salted_pw = en.decrypt(salted_pw, hashKey);
            String s2 = new String(salted_pw, StandardCharsets.UTF_8);
            //System.out.println("Text Decryted : " + s2);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return authenticated;
    }
    public byte[] saltPassword(String pw, String salt){
        byte[] pw_bytes = pw.getBytes(StandardCharsets.UTF_8);
        byte[] salt_bytes = salt.getBytes(StandardCharsets.UTF_8);
        int i = 0;
        for(i = 0; i<pw_bytes.length; i++){
            pw_bytes[i] = (byte)(pw_bytes[i] ^ salt_bytes[i%salt_bytes.length]);
        }
        return pw_bytes;
    }
    public void newUser(String username, String password){
        Encrypter en = new Encrypter();
        Random r = new SecureRandom();
        String new_salt = new BigInteger(130, r).toString(32);

        byte[] salted_pw = saltPassword(password, new_salt);

        String salt_string = new String(salted_pw, StandardCharsets.UTF_8);
        System.out.println("Password : " + salt_string);

        byte[] hashed = en.encrypt(salted_pw, hashKey);

        String hash_string = new String(hashed, StandardCharsets.UTF_8);

        //salted_pw = en.decrypt(salted_pw, hashKey);
        //String s2 = new String(salted_pw, StandardCharsets.UTF_8);
        //System.out.println("Password Decryted : " + s2);
        System.out.println("Hashed string length in newUser: " + hash_string.length());
        Path file = Paths.get("shadowfile.txt");
        try{
            if(!Files.exists(file)){
                Files.createFile(file);
            }
            Files.write(file, (username+"\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            Files.write(file, (new_salt+"\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            Files.write(file, (hash_string+"\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
            
    }
    public byte[] retrieveFile(Path filepath){
        byte[] check = new byte[1];
        check[0] = 0;
        try{
            Encrypter en = new Encrypter();
            byte[] data = Files.readAllBytes(filepath);

            data = en.encrypt(data, sharedKey);
            return data;
        } catch (IOException e){
            e.printStackTrace();
            
        }
        return check;
    }
}