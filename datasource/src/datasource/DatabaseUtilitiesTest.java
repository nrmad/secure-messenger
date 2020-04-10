package datasource;

import org.junit.After;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;

import static org.junit.Assert.*;

public class DatabaseUtilitiesTest {


    private DatabaseUtilites databaseUtilities = DatabaseUtilites.getInstance();
//    private Connection conn;

    @After
    public void tearDown() {

        databaseUtilities.tempMethod();

    }

    @org.junit.Test
    public void getInstance() {
        assertEquals(databaseUtilities, DatabaseUtilites.getInstance());
    }

    @org.junit.Test
    public void addContact() {
        Contact contact = new Contact("f1dg13d7f8sfd", "nrmad", "127.0.0.1", 1025);
        assertTrue(databaseUtilities.addContact(contact));
        contact = new Contact("f1dg13d7f8sfd", "nrmad", "127.0.0.1", 1025);
        assertFalse(databaseUtilities.addContact(contact));
        contact = new Contact("f1dg13d7f8sfd1", "nrmad", "1234-0-0-1", 1025);
        assertFalse(databaseUtilities.addContact(contact));
        contact = new Contact("f1dg13d7f8sfd2", "nrmad", "127-a-a-a", 1);
        assertFalse(databaseUtilities.addContact(contact));
        contact = new Contact("f1dg13d7f8sfd3", "nrmad", "127.0.0.0", 65536);
        assertFalse(databaseUtilities.addContact(contact));
        contact = new Contact("f1dg13d7f8sfd4", "nrmad", "127-a-a-a", -1);
        assertFalse(databaseUtilities.addContact(contact));

    }

    @org.junit.Test
    public void addAccount() {
        try {
        byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
        String stringSalt = Base64.getEncoder().encodeToString(salt);
        String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);



        Account account = new Account("james", tempHash, stringSalt, 100000);
        Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 1025);
        assertTrue(databaseUtilities.addAccount(account, contact));
        account = new Account("", tempHash, stringSalt, 100000);
        contact = new Contact("abd", "accountboi", "127.0.0.1", 1025);
        assertFalse(databaseUtilities.addAccount(account, contact));
        account = new Account("jack", "", stringSalt, 100000);
        contact = new Contact("abe", "accountboi", "127.0.0.1", 1025);
        assertFalse(databaseUtilities.addAccount(account, contact));
        account = new Account("jo", tempHash, "", 100000);
        contact = new Contact("abf", "accountboi", "127.0.0.1", 1025);
        assertFalse(databaseUtilities.addAccount(account, contact));

    }catch(NoSuchAlgorithmException |InvalidKeySpecException e){
        System.out.println(e.getMessage());
    }
}

    @org.junit.Test
    public void addChat(){

        try {
            byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
            String stringSalt = Base64.getEncoder().encodeToString(salt);
            String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);

            Account account = new Account("james", tempHash, stringSalt, 100000);
            Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 2);
            databaseUtilities.addAccount(account, contact);
            contact = new Contact("abc", "nrmad", "127.0.0.1", 1);
            databaseUtilities.addContact(contact);
            Chat chat = new Chat(1,"abd");
            assertTrue(databaseUtilities.addChat(chat));

        // ??? ADD A FALSE
        }catch(NoSuchAlgorithmException |InvalidKeySpecException e){
            System.out.println(e.getMessage());
        }
    }


    @org.junit.Test
    public void addMessage() {

        try {
            byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
            String stringSalt = Base64.getEncoder().encodeToString(salt);
            String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);

            Account account = new Account("james", tempHash, stringSalt, 100000);
            Contact contact = new Contact("abd", "accountboi", "127.0.0.1", 1025);
            databaseUtilities.addAccount(account, contact);
            contact = new Contact("abc", "nrmad", "127.0.0.1", 1025);
            databaseUtilities.addContact(contact);
            Chat chat = new Chat(1,"abd");
            databaseUtilities.addChat(chat);

            Message message = new Message( "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.PENDING);
            assertTrue(databaseUtilities.addMessage(message, 1, "abd"));
            message = new Message( "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.SENT);
            assertTrue(databaseUtilities.addMessage(message, 1, "abd"));
            message = new Message("sdoijfsdoi", System.currentTimeMillis(), MessageStatus.RECIEVED);
            assertTrue(databaseUtilities.addMessage(message, 1, "abd"));

        }catch(NoSuchAlgorithmException |InvalidKeySpecException e){
            System.out.println(e.getMessage());
        }

        // ??? ADD A FALSE

    }

    @org.junit.Test
    public void addRetained(){

        try {
            byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
            String stringSalt = Base64.getEncoder().encodeToString(salt);
            String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);


            Account account = new Account("james", tempHash, stringSalt, 100000);
            Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 1025);
            databaseUtilities.addAccount(account, contact);
            contact = new Contact("a","boi1","127.0.0.1",1025);
            databaseUtilities.addContact(contact);
            contact = new Contact("b","boi2","127.0.0.1",1025);
            databaseUtilities.addContact(contact);
            contact = new Contact("c","boi3","127.0.0.1",1025);
            databaseUtilities.addContact(contact);

            Synchronize synchronize = new Synchronize(1,"a", SyncType.CONTACT);
            assertTrue(databaseUtilities.addSynchronize(synchronize));
            synchronize = new Synchronize(1,"b", SyncType.IP);
            assertTrue(databaseUtilities.addSynchronize(synchronize));
            synchronize = new Synchronize(1,"c", SyncType.TLSPORT);
            assertTrue(databaseUtilities.addSynchronize(synchronize));

        }catch(NoSuchAlgorithmException |InvalidKeySpecException e){
            System.out.println(e.getMessage());
        }
    }

    private byte[] getSalt(){
        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[64], hashWithSalt = new byte[64];
        RANDOM.nextBytes(salt);
//        String stringSalt = Base64.getEncoder().encodeToString(salt);
        return salt;
    }

    private byte[] getKey(byte[] salt, String password)
    throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100000, 512);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return secretKeyFactory.generateSecret(spec).getEncoded();
    }


}