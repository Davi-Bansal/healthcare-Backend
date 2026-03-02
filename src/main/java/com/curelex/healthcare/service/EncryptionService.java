package com.curelex.healthcare.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    // MUST be exactly 16 chars for AES-128
    private static final String SECRET = "1234567890123456";

    private SecretKeySpec getKey(){
        return new SecretKeySpec(SECRET.getBytes(), "AES");
    }

    // ---------------- ENCRYPT ----------------
    public String encrypt(String data){

        if(data == null || data.isBlank()){
            return "";
        }

        try{

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());

            byte[] encrypted = cipher.doFinal(data.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);

        }catch(Exception e){
            throw new RuntimeException("Encryption failed");
        }
    }

    // ---------------- DECRYPT ----------------
    public String decrypt(String encryptedData){

        if(encryptedData == null || encryptedData.isBlank()){
            return "";
        }

        try{

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getKey());

            byte[] decoded = Base64.getDecoder().decode(encryptedData);

            return new String(cipher.doFinal(decoded));

        }catch(Exception e){
            throw new RuntimeException("Decryption failed");
        }
    }
}
