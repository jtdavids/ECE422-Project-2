import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.nio.charset.StandardCharsets;

public class ClientThread {
    static final int PORT = 16000;

    public static void main(String args[]) {
        //InputStream in = null;
        BufferedReader br = null;
        PrintWriter out = null;
        InputStream in = null;
        Socket socket = null;
        Scanner sc = new Scanner(System.in);
        
        Encrypter en = new Encrypter();
        // KEY GENERATION TESTING -------------------------------
        try{
            SecureRandom random = new SecureRandom();
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(random);
            Key key1 = keygen.generateKey();
            System.out.println("Generated key: " + key1.getEncoded());
            
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return;
        }
        // KEY GENERATION TESTING --------------------------------
        // ENCRYPTION TESTING ----------------------------
        String str = "Hello World!!!!!12345678";
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
        // ENCRYPTION TESTING ----------------------------

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