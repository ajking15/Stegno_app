package com.example.stegnoapp.stegno.Tools;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptAlgo {

    //Encryption Method
    /*
    @parameter : Message {String}, Secret key {String}
    @return : Encrypted Message {String}
     */
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(String myKey)
    {
        MessageDigest shaKey = null;
        try {
            key = myKey.getBytes("UTF-8");
            shaKey = MessageDigest.getInstance("SHA-1");
            key = shaKey.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("error", e.toString());
        }
        catch (UnsupportedEncodingException e) {
            Log.e("error", e.toString());
        }
    }
    public static String encryptMessage(String message, String secret_key) throws Exception {
        Log.d("Crypto", "cipher here: AES");
        String encrypt = null;
        try
        {
            setKey(secret_key);
            Log.d("crypto", "Encoding: Getting cipher instance");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            Log.d("crypto", "Encoding the message");
            encrypt = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            Log.e("error", "Error while encrypting: " + e.toString());
        }
        Log.d("crypt", "Encrypted Message: " + encrypt);
        return encrypt;
    }

    //Decryption Method
    /*
    @parameter : Encrypted Message {String}, Secret key {String}
    @return : Message {String}
     */
    public static String decryptMessage(String encrypted_message, String secret_key) throws Exception {
        try
        {
            setKey(secret_key);
            Log.d("crypto", "Decoding: Getting cipher instance");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            Log.d("crypto", "Decoding the message");
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted_message)));
        }
        catch (Exception e)
        {
            Log.e("Error","Error while decrypting: " + e.toString());
        }
        return null;
    }

}
