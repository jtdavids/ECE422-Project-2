public class Encrypter {
    native void encryptInC(byte[] val, long[] key); /* (1) */
    native void decryptInC(byte[] val, long[] key);
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
}