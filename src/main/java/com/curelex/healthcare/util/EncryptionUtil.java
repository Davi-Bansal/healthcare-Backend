package com.curelex.healthcare.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String SECRET = "MySuperSecretKey"; // 16 chars exactly

    private static SecretKeySpec getKey(){
        return new SecretKeySpec(SECRET.getBytes(), "AES");
    }

    public static String encrypt(String data){

        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());

            byte[] encrypted = cipher.doFinal(data.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);

        }catch(Exception e){
            throw new RuntimeException("Encryption failed");
        }
    }

    public static String decrypt(String encrypted){

        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getKey());

            byte[] decoded = Base64.getDecoder().decode(encrypted);

            return new String(cipher.doFinal(decoded));

        }catch(Exception e){
            throw new RuntimeException("Decryption failed");
        }
    }
}
