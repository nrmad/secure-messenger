package encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Utilities {

    public static String encrypt(String plainText, PublicKey publicKey){

        try {
            Cipher encryptCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

            try {
                encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));
                return Base64.getEncoder().encodeToString(cipherText);

            } catch(InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
                System.out.println(e.getMessage());
            }

        }catch(NoSuchAlgorithmException | NoSuchPaddingException e){
            System.out.println(e.getMessage());
        }

        return null;
    }


    public static String decrpyt(String cipherText, PrivateKey privateKey){

        try{
            Cipher decryptCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

            try{
                decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] bytes = Base64.getDecoder().decode(cipherText);
                return new String(decryptCipher.doFinal(bytes), UTF_8);

            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
                System.out.println(e.getMessage());
            }

        } catch(NoSuchAlgorithmException | NoSuchPaddingException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

}
