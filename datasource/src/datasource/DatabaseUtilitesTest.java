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
        assertTrue(databaseUtilites.addContact("f1dg13d7f8sfd","nrmad","127.0.0.1",1));
        assertFalse(databaseUtilites.addContact("f1dg13d7f8sfe","azt4er", "127-0-0-1",1));
        assertFalse(databaseUtilites.addContact("f1dg13d7f8sff","benny", "1234.0.0.1",1));
        assertFalse(databaseUtilites.addContact("f1dg13d7f8sfg","azt4er", "127.a.a.a",1));

        assertFalse(databaseUtilites.addContact("35gtrmku76bgh45kj56ikyf","benny", "127.0.0.1",65536));
        assertFalse(databaseUtilites.addContact("35gtrmku76bgh45kj56ikyg","azt4er", "127.0.0.1",-1));

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

        assertTrue(databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt, "abc", "accountboi", "127.0.0.1", 2));

        assertFalse(databaseUtilites.addAccount("1234567891011121314151617181920212223242526272829303132333" +
                "435363738394041424344454647484950515253545556575859606162636465666768697071727374757677787980818" +
                "28384858687888990919293949596979899100101102103104105106107108109110111112113114115116117118119120" +
                "12112212312412512612712812913013113213313413513613713813914014114214314414514614714814915015115215" +
                "31541551561571581591601611621631641651661671681691701711721731741751761771781791801811821831841851" +
                "86187188189190191192193194195196197198199200201202203204205206207208209210211212213214215216217218" +
                "2192202212222232242252262272282292302312322332342352362372382392402412422432442452462472482492502" +
                "51252253254255256", "password"+stringSalt, stringSalt, "abc", "accountboi1", "127.0.0.1", 2));

        assertFalse(databaseUtilites.addAccount("jack", "1234567891011121314151617181920212223242526272829303132333" +
                "435363738394041424344454647484950515253545556575859606162636465666768697071727374757677787980818" +
                "28384858687888990919293949596979899100101102103104105106107108109110111112113114115116117118119120" +
                "12112212312412512612712812913013113213313413513613713813914014114214314414514614714814915015115215" +
                "31541551561571581591601611621631641651661671681691701711721731741751761771781791801811821831841851" +
                "86187188189190191192193194195196197198199200201202203204205206207208209210211212213214215216217218" +
                "2192202212222232242252262272282292302312322332342352362372382392402412422432442452462472482492502" +
                "51252253254255256"+stringSalt, stringSalt, "abc", "accountboi", "127.0.0.1", 2));

        assertFalse(databaseUtilites.addAccount("jo", "password"+stringSalt, "1234567891011121314151617181920212223242526272829303132333" +
                "435363738394041424344454647484950515253545556575859606162636465666768697071727374757677787980818" +
                "28384858687888990919293949596979899100101102103104105106107108109110111112113114115116117118119120" +
                "12112212312412512612712812913013113213313413513613713813914014114214314414514614714814915015115215" +
                "31541551561571581591601611621631641651661671681691701711721731741751761771781791801811821831841851" +
                "86187188189190191192193194195196197198199200201202203204205206207208209210211212213214215216217218" +
                "2192202212222232242252262272282292302312322332342352362372382392402412422432442452462472482492502" +
                "51252253254255256", "abc", "accountboi", "127.0.0.1", 2));


    }

    @org.junit.Test
    public void addChat(){

        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[64];
        RANDOM.nextBytes(salt);
        String stringSalt = Base64.getEncoder().encodeToString(salt);

        databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt, "abc", "accountboi", "127.0.0.1", 2);
        databaseUtilites.addContact("abd","nrmad","127.0.0.1",1);

        assertTrue(databaseUtilites.addChat(1,"abd"));
        // ??? ADD A FALSE
    }


    @org.junit.Test
    public void addMessage() {


        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[64];
        RANDOM.nextBytes(salt);
        String stringSalt = Base64.getEncoder().encodeToString(salt);

        databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt, "abc", "accountboi", "127.0.0.1", 2);
        databaseUtilites.addContact("abd","nrmad","127.0.0.1",1);
        databaseUtilites.addChat(1,"abd");

        assertTrue(databaseUtilites.addMessage("abd", 1, "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.PENDING));
        assertTrue(databaseUtilites.addMessage("abd", 1, "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.SENT));
        assertTrue(databaseUtilites.addMessage("abd", 1, "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.RECIEVED));


        // ??? ADD A FALSE

    }

    @org.junit.Test
    public void addRetained(){

        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[64];
        RANDOM.nextBytes(salt);
        String stringSalt = Base64.getEncoder().encodeToString(salt);

        databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt, "abc", "accountboi", "127.0.0.1", 2);
        databaseUtilites.addContact("a","boi1","127.0.0.1",1);
        databaseUtilites.addContact("b","boi2","127.0.0.1",1);
        databaseUtilites.addContact("c","boi3","127.0.0.1",1);

        assertTrue(databaseUtilites.addRetained(1,"a", RetainedDatatype.CONTACT));
        assertTrue(databaseUtilites.addRetained(1,"b", RetainedDatatype.IP));
        assertTrue(databaseUtilites.addRetained(1,"c", RetainedDatatype.TLSPORT));


    }
}