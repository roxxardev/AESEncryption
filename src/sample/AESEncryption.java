package sample;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AESEncryption {

    private static final String ALGORITHM = "AES";
    private byte[] keyValue;

    public byte[] getKeyValue() {
        return keyValue;
    }

    public AESEncryption(String key) throws UnsupportedEncodingException {
        keyValue = key.getBytes("UTF-8");
    }

    public AESEncryption(byte[] keyValue) {
        this.keyValue = keyValue;
    }

    public AESEncryption() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        keyValue = secretKey.getEncoded();
    }

    public String encryptAndEncodeBASE64(String data) throws Exception {
        return encodeBASE64(encryptText(data));
    }

    public String decodeBASE64AndDecrypt(String encryptedData) throws Exception {
        return decryptText(decodeBASE64(encryptedData));
    }

    public byte[] encryptText(String data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(data.getBytes());
    }

    public String decryptText(byte[] encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        return new String(c.doFinal(encryptedData));
    }

    public String encodeBASE64(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }

    public byte[] decodeBASE64(String s) throws IOException {
        return new BASE64Decoder().decodeBuffer(s);
    }

    public void encryptFile(File inputFile, File outputFile) throws Exception{
        crypto(Cipher.ENCRYPT_MODE, inputFile, outputFile);
    }

    public void decryptFile(File inputFile, File outputFile) throws Exception{
        crypto(Cipher.DECRYPT_MODE, inputFile, outputFile);
    }

    private void crypto(int cipherMode, File inputFile, File outputFile) throws Exception{
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(cipherMode, generateKey());

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = c.doFinal(inputBytes);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        outputStream.close();
        inputStream.close();
    }

    private Key generateKey() {
        return new SecretKeySpec(keyValue, ALGORITHM);
    }
}

