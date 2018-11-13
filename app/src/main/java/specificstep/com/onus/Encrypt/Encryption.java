package specificstep.com.onus.Encrypt;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import specificstep.com.onus.GlobalClasses.Constants;

public class Encryption {
    public static void main(String[] args) {
        try {
            String secretKey = "BSNLChannel";
            String Encrptdata = Encrptdata("9493789819", secretKey, "Hello World");
            System.out.println("Encrpted " + Encrptdata);
            System.out.println("Decryptdata " + Decryptdata(Encrptdata, secretKey));
        } catch (Exception ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String Decryptdata(String enc_data, String secretKey) {
        String str = null;
        try {
            String appender = Constants.APPENDER_VALUE;
            int dataPartLength = Integer.parseInt(enc_data.substring(0, 2));
            String rest_data = enc_data.substring(2);
            str = _decrypt(rest_data.substring(dataPartLength), _encrypt(new StringBuffer(rest_data.substring(0, dataPartLength)).reverse().toString() + appender, secretKey));
        } catch (Exception ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, str, ex);
        }
        return str;
    }

    public static String Encrptdata(String dataPart, String secretKey, String content) {
        String str = null;
        try {
            String appender = Constants.APPENDER_VALUE;
            String size = dataPart.length() + "";
            if (size.length() <= 1) {
                size = 0 + size;
            }
            str = size + dataPart + _encrypt(content, _encrypt(new StringBuffer(dataPart).reverse().toString() + appender, secretKey));
        } catch (Exception ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str;
    }

    public static String _encrypt(String message, String secretKey) throws Exception {
        SecretKey key = new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(secretKey.getBytes("utf-8")), 24), "DESede");
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(1, key);
        return new String(Base64.encodeBase64(cipher.doFinal(message.getBytes("utf-8"))));
    }

    private static String _decrypt(String encryptedText, String secretKey) throws Exception {
        byte[] message = Base64.decodeBase64(encryptedText.getBytes("utf-8"));
        SecretKey key = new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(secretKey.getBytes("utf-8")), 24), "DESede");
        Cipher decipher = Cipher.getInstance("DESede");
        decipher.init(2, key);
        return new String(decipher.doFinal(message), Key.STRING_CHARSET_NAME);
    }
}
