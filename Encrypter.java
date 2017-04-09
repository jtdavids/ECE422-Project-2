import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.ByteBuffer;

public class Encrypter {
    native void encryptInC(byte[] val, long[] key); /* (1) */
    native void decryptInC(byte[] val, long[] key);
    private long[] sharedKey = { 886223543, 114558990, 222323881, 234123879 };
    static {
        System.loadLibrary("Encrypter"); /* (2) */
    }
    public Encrypter() {

    }

    public byte[] encrypt(byte[] v, long[] k){
    	//System.out.println("attempting encryption...");
    	encryptInC(v, k);
    	return v;
    }
    public byte[] decrypt(byte[] v, long[] k){
    	decryptInC(v, k);
    	return v;
    }
    public void sendMessage(Socket sock, String message){
        try{
            OutputStream output = sock.getOutputStream();
            byte[] encrypted_message = encryptMessage(message);
            ByteBuffer buff = ByteBuffer.allocate(4);
            buff.putInt(encrypted_message.length);
            byte[] dataLength = buff.array();
            //System.out.println("Message Sent: " + message);
            //System.out.println("Message size: " + encrypted_message.length);
            output.write(dataLength);
            output.write(encrypted_message);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    public String decryptMessage(byte[] message){
       
        byte[] byteData = decrypt(message, sharedKey);
        String data = new String(byteData,  StandardCharsets.UTF_8);
        return data;
    }
    public byte[] encryptMessage(String message){
         
        byte[] byteMessage = message.getBytes(StandardCharsets.UTF_8);
        byte[] data = encrypt(byteMessage, sharedKey);
        return data;
    }
    public String readMessage(Socket sock){
        try{
            InputStream input = sock.getInputStream();
            byte[] messageLength = new byte[4];
            input.read(messageLength);
            ByteBuffer wrapped = ByteBuffer.wrap(messageLength);
            int length = wrapped.getInt();

            byte[] message = new byte[length];

            input.read(message);

            String recieved_message = decryptMessage(message);
            //System.out.println("MESSAGE SIZE: "+ length);
            //System.out.println("MESSAGE: " + recieved_message);
            return recieved_message;
        } catch(IOException e){
            throw new RuntimeException(e);
        }

    }
    public void transferFile(Socket sock, Path filepath){
        try{
            OutputStream output = sock.getOutputStream();
            byte[] data = Files.readAllBytes(filepath);
        	data = encrypt(data, sharedKey);
            ByteBuffer buff = ByteBuffer.allocate(4);
            buff.putInt(data.length);
            byte[] dataLength = buff.array();

            output.write(dataLength);
            output.write(data);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    public byte[] recieveFile(Socket sock){
    	try{
            InputStream input = sock.getInputStream();
            byte[] fileLength = new byte[4];
            input.read(fileLength);
            ByteBuffer wrapped = ByteBuffer.wrap(fileLength);
            int length = wrapped.getInt();

            byte[] file = new byte[length];

            input.read(file);

            byte[] decrypted_file = decrypt(file, sharedKey);
            return decrypted_file;
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}