package datasource;

import org.junit.After;
import org.junit.BeforeClass;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

import static org.junit.Assert.*;

public class DatabaseUtilitesTest {


    private DatabaseUtilites databaseUtilites;
    private Connection conn;

    @BeforeClass
    public void setup(){
        try{
            databaseUtilites = DatabaseUtilites.getInstance();
        }catch (SQLException e){

        }
    }
    @After
    public void tearDown(){

        databaseUtilites.tempMethod();

    }

    @org.junit.Test
    public void getInstance() {
        try {
            assertEquals(databaseUtilites, DatabaseUtilites.getInstance());
        }catch (SQLException e){}
    }






//    @org.junit.Test
//    public void addChat(){
//
//        Random RANDOM = new SecureRandom();
//        byte[] salt = new byte[64];
//        RANDOM.nextBytes(salt);
//        String stringSalt = Base64.getEncoder().encodeToString(salt);
//
//        databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt, "abc", "accountboi", "127.0.0.1", 2);
//        databaseUtilites.addContact("abd","nrmad","127.0.0.1",1);
//
//        assertTrue(databaseUtilites.addChat(1,"abd"));
//        // ??? ADD A FALSE
//    }
//
//
//    @org.junit.Test
//    public void addMessage() {
//
//
//        Random RANDOM = new SecureRandom();
//        byte[] salt = new byte[64];
//        RANDOM.nextBytes(salt);
//        String stringSalt = Base64.getEncoder().encodeToString(salt);
//
//        databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt, "abc", "accountboi", "127.0.0.1", 2);
//        databaseUtilites.addContact("abd","nrmad","127.0.0.1",1);
//        databaseUtilites.addChat(1,"abd");
//
//        assertTrue(databaseUtilites.addMessage("abd", 1, "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.PENDING));
//        assertTrue(databaseUtilites.addMessage("abd", 1, "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.SENT));
//        assertTrue(databaseUtilites.addMessage("abd", 1, "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.RECIEVED));
//
//
//        // ??? ADD A FALSE
//
//    }
//
//    @org.junit.Test
//    public void addRetained(){
//
//        Random RANDOM = new SecureRandom();
//        byte[] salt = new byte[64];
//        RANDOM.nextBytes(salt);
//        String stringSalt = Base64.getEncoder().encodeToString(salt);
//
//        databaseUtilites.addAccount("james", "password"+stringSalt, stringSalt, "abc", "accountboi", "127.0.0.1", 2);
//        databaseUtilites.addContact("a","boi1","127.0.0.1",1);
//        databaseUtilites.addContact("b","boi2","127.0.0.1",1);
//        databaseUtilites.addContact("c","boi3","127.0.0.1",1);
//
//        assertTrue(databaseUtilites.addRetained(1,"a", RetainedDatatype.CONTACT));
//        assertTrue(databaseUtilites.addRetained(1,"b", RetainedDatatype.IP));
//        assertTrue(databaseUtilites.addRetained(1,"c", RetainedDatatype.TLSPORT));
//
//
//    }
}