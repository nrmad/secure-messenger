package datasource;

import org.junit.After;
import org.junit.Test;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.*;

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
    public void getAccount(){

        try {
            byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
            String stringSalt = Base64.getEncoder().encodeToString(salt);
            String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);

            Account account = new Account("james", tempHash, stringSalt, 100000);
            Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 1025);
            databaseUtilities.addAccount(account, contact);

            account = new Account(5, account.getUsername(), account.getKey(), account.getSalt(), account.getIterations());
            Account account1 = databaseUtilities.getAccount(account.getUsername());
//            System.out.println(account.getUid()+ account.getUsername() + account.getKey() + account.getSalt() + account.getIterations());
//            System.out.println(account1.getUid()+ account1.getUsername() + account1.getKey() + account1.getSalt() + account1.getIterations());


            assertEquals(account, databaseUtilities.getAccount(account.getUsername()));
            assertNotEquals(account, databaseUtilities.getAccount("jack"));

        }catch(NoSuchAlgorithmException |InvalidKeySpecException | SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @org.junit.Test
    public void updateAccount(){
        try {
            byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
            String stringSalt = Base64.getEncoder().encodeToString(salt);
            String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);

            Account account = new Account("james", tempHash, stringSalt, 100000);
            Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 1025);
            databaseUtilities.addAccount(account, contact);

            account = databaseUtilities.getAccount(account.getUsername());
            salt = getSalt();
            hashWithSalt = getKey(salt, "password", 150000);
            stringSalt = Base64.getEncoder().encodeToString(salt);
            tempHash = Base64.getEncoder().encodeToString(hashWithSalt);

            assertTrue(databaseUtilities.updateAccount(new Account(account.getUid(), account.getUsername(), tempHash, stringSalt, 150000)));

        }catch(NoSuchAlgorithmException |InvalidKeySpecException | SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @org.junit.Test
    public void getContacts(){
        List<Contact> contacts = new ArrayList<>();

        try {
            byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
            String stringSalt = Base64.getEncoder().encodeToString(salt);
            String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);

            Account account = new Account("james", tempHash, stringSalt, 100000);
            Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 1025);
            databaseUtilities.addAccount(account, contact);

            try {
                account = databaseUtilities.getAccount(account.getUsername());
            }catch (SQLException e){}

            contacts.add(new Contact("a1", "nrmad", "127.0.0.1", 1025));
            contacts.add(new Contact("a2", "nrmad", "127.0.0.1", 1025));
            contacts.add(new Contact("a3", "nrmad", "127.0.0.1", 1025));

            databaseUtilities.addContacts(contacts, account);

            contacts.add(contact);

            assertEquals(contacts, databaseUtilities.getContacts(account));

        }catch(NoSuchAlgorithmException |InvalidKeySpecException | SQLException e){
            System.out.println(e.getMessage());
        }

        }

    // MUST CHECK WITH BOGUS INPUT
    @org.junit.Test
    public void addContacts() {

        List<Contact> contacts = new ArrayList<>();

        try {
            byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
            String stringSalt = Base64.getEncoder().encodeToString(salt);
            String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);

            Account account = new Account("james", tempHash, stringSalt, 100000);
            Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 1025);
            databaseUtilities.addAccount(account, contact);

            try {
                account = databaseUtilities.getAccount(account.getUsername());
            }catch (SQLException e){}

            contacts.add(new Contact("f1dg13d7f8sfd", "nrmad", "127.0.0.1", 1025));
        assertTrue(databaseUtilities.addContacts(contacts, account));

        contacts.clear();
        contacts.add(new Contact("f1dg13d7f8sfd", "nrmad", "127.0.0.1", 1025));
        assertFalse(databaseUtilities.addContacts(contacts, account));

        contacts.clear();
        contacts.add(new Contact("f1dg13d7f8sfd1", "nrmad", "1234-0-0-1", 1025));
        assertFalse(databaseUtilities.addContacts(contacts, account));

        contacts.clear();
        contacts.add(new Contact("f1dg13d7f8sfd2", "nrmad", "127-a-a-a", 1));
        assertFalse(databaseUtilities.addContacts(contacts, account));

        contacts.clear();
        contacts.add(new Contact("f1dg13d7f8sfd3", "nrmad", "127.0.0.0", 65536));
        assertFalse(databaseUtilities.addContacts(contacts, account));

        contacts.clear();
        contacts.add( new Contact("f1dg13d7f8sfd4", "nrmad", "127-a-a-a", -1));
        assertFalse(databaseUtilities.addContacts(contacts, account));

        contacts.clear();
        contacts.add(new Contact("a1", "nrmad", "127.0.0.1", 1025));
        contacts.add(new Contact("a2", "nrmad", "127.0.0.1", 1025));
        contacts.add(new Contact("a3", "nrmad", "127.0.0.1", 1025));

        assertTrue(databaseUtilities.addContacts(contacts, account));


        }catch(NoSuchAlgorithmException |InvalidKeySpecException e){
            System.out.println(e.getMessage());
        }
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

            List<Contact> contacts = new ArrayList<>();

            Account account = new Account("james", tempHash, stringSalt, 100000);
            Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 1025);
            databaseUtilities.addAccount(account, contact);

            try {
                account = databaseUtilities.getAccount(account.getUsername());
            }catch (SQLException e){}

            contacts.add(new Contact("abd", "nrmad", "127.0.0.1", 1025));
            databaseUtilities.addContacts(contacts, account);
            Chat chat = new Chat(account.getUid(),"abd");
            assertTrue(databaseUtilities.addChat(chat));

        // ??? ADD A FALSE
        }catch(NoSuchAlgorithmException |InvalidKeySpecException e){
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void addMessage() {

        try {
            byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
            String stringSalt = Base64.getEncoder().encodeToString(salt);
            String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);

            List<Contact> contacts = new ArrayList<>();

            Account account = new Account("james", tempHash, stringSalt, 100000);
            Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 1025);
            databaseUtilities.addAccount(account, contact);
            contact = new Contact("abd", "nrmad", "127.0.0.1", 1025);
            contacts.add(contact);
            databaseUtilities.addContacts(contacts, account);
            Chat chat = new Chat(1,"abd");
            databaseUtilities.addChat(chat);

            account = new Account(1,"james", tempHash, stringSalt, 100000);

            Message message = new Message( "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.PENDING);
            assertTrue(databaseUtilities.addMessage(message, account, contact));
            message = new Message( "sdoijfsdoi", System.currentTimeMillis(), MessageStatus.SENT);
            assertTrue(databaseUtilities.addMessage(message, account, contact));
            message = new Message("sdoijfsdoi", System.currentTimeMillis(), MessageStatus.RECIEVED);
            assertTrue(databaseUtilities.addMessage(message, account, contact));

        }catch(NoSuchAlgorithmException |InvalidKeySpecException e){
            System.out.println(e.getMessage());
        }

        // ??? ADD A FALSE

    }

    @org.junit.Test
    public void addSynchronize(){

        try {
            byte[] salt = getSalt(), hashWithSalt = getKey(salt, "password");
            String stringSalt = Base64.getEncoder().encodeToString(salt);
            String tempHash = Base64.getEncoder().encodeToString(hashWithSalt);


            Account account = new Account("james", tempHash, stringSalt, 100000);
            List<Contact> contacts = new ArrayList<>();
            Contact contact = new Contact("abc", "accountboi", "127.0.0.1", 1025);
            databaseUtilities.addAccount(account, contact);
            contacts.add(new Contact("a","boi1","127.0.0.1",1025));
            databaseUtilities.addContacts(contacts, account);
            contacts.clear();
            contacts.add(new Contact("b","boi2","127.0.0.1",1025));
            databaseUtilities.addContacts(contacts, account);
            contacts.remove(0);
            contacts.add( new Contact("c","boi3","127.0.0.1",1025));
            databaseUtilities.addContacts(contacts, account);

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
            throws NoSuchAlgorithmException, InvalidKeySpecException{
        return getKey(salt, password, 100000);
    }
    private byte[] getKey(byte[] salt, String password, int iterations)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 512);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return secretKeyFactory.generateSecret(spec).getEncoded();
    }


}