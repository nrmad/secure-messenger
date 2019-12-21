package datasource;

import org.junit.After;

import java.security.SecureRandom;
import java.sql.Connection;
import java.util.Base64;
import java.util.Random;

import static org.junit.Assert.*;

public class DatabaseUtilitesTest {


    private DatabaseUtilites databaseUtilites = DatabaseUtilites.getInstance();
    private Connection conn;

    @After
    public void tearDown(){

        databaseUtilites.tempMethod();

    }

    @org.junit.Test
    public void getInstance() {
        assertEquals(databaseUtilites, DatabaseUtilites.getInstance());
    }

    @org.junit.Test
    public void addContact() {
        assertTrue(databaseUtilites.addContact(1,"nrmad","127.0.0.1"));
        assertFalse(databaseUtilites.addContact(2,"azt4er", "127-0-0-1"));
        assertFalse(databaseUtilites.addContact(3,"benny", "1234.0.0.1"));
        assertFalse(databaseUtilites.addContact(4,"azt4er", "127.a.a.a"));
        assertFalse(databaseUtilites.addContact(1,"azt4er","127.0.0.1"));

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

        databaseUtilites.addContact(1,"accountboi","127.0.0.1");
        assertTrue(databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt));
        //assertFalse(databaseUtilites.addAccount("sneakboi","passSneak"+stringSalt, stringSalt));
    }

    @org.junit.Test
    public void addChat(){
        databaseUtilites.addContact(1, "accountboi", "127.0.0.1");

        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[64];
        RANDOM.nextBytes(salt);
        String stringSalt = Base64.getEncoder().encodeToString(salt);

        databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt);
        assertTrue(databaseUtilites.addChat(1,1));
    }


    @org.junit.Test
    public void addMessage() {
        databaseUtilites.addContact(1, "accountboi", "127.0.0.1");

        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[64];
        RANDOM.nextBytes(salt);
        String stringSalt = Base64.getEncoder().encodeToString(salt);

        databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt);
        databaseUtilites.addChat(1,1);
        assertTrue(databaseUtilites.addMessage(1,1,"yeet","12-12-2019 11:48:22", MessageStatus.PENDING));
    }
}