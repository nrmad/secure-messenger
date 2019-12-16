package datasource;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import static org.junit.Assert.*;

public class DatabaseUtilitesTest {


    private DatabaseUtilites databaseUtilites = DatabaseUtilites.getInstance();

    @org.junit.Test
    public void getInstance() {
        assertEquals(databaseUtilites, DatabaseUtilites.getInstance());
    }

    @org.junit.Test
    public void addContact() {
        assertTrue(databaseUtilites.addContact("nrmad","127.0.0.1"));
        assertFalse(databaseUtilites.addContact("azt4er", "127-0-0-1"));
        assertFalse(databaseUtilites.addContact("benny", "1234.0.0.1"));
        assertFalse(databaseUtilites.addContact("azt4er", "127.a.a.a"));

    }

    @org.junit.Test
    public void addAccount() {
        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[64];
        RANDOM.nextBytes(salt);
        String stringSalt = Base64.getEncoder().encodeToString(salt);

//        PBEKeySpec spec;
//        spec = new PBEKeySpec("jamespass".toCharArray(),salt, 10, 256);
//
//        try {
//            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
//            SecretKey key = skf.generateSecret(spec);
//            byte[] res = key.getEncoded()
//
//        } catch(NoSuchAlgorithmException | InvalidKeySpecException e){
//            System.out.println(e.getMessage());
//        }

        databaseUtilites.addContact("accountboi","127.0.0.1");
        assertTrue(databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt));
        //assertFalse(databaseUtilites.addAccount("sneakboi","passSneak"+stringSalt, stringSalt));
    }

    @org.junit.Test
    public void addChat(){

    }

    @org.junit.Test
    public void addMessage() {
        fail();
    }
}