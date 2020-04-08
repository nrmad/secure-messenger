package datasource;

import java.io.File;
import java.sql.*;
import java.util.regex.Pattern;

public class DatabaseUtilites {

private static DatabaseUtilites databaseUtilites = new DatabaseUtilites();
private Connection conn;
private Pattern ipv4Pattern = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
//private Pattern dateTime = Pattern.compile("^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}$");

public String DB_NAME = "secure-messenger.db";
// connection string could not be psf because concatenation with File.separator renders the string null
public String CONNECTION_STRING = "jdbc:sqlite:" + "resources" + File.separator + DB_NAME;

private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS contacts(cid TEXT PRIMARY KEY, alias TEXT NOT NULL, ipv4 TEXT NOT NULL, tlsport INTEGER NOT NULL)";
private static final String CREATE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS accounts(uid INTEGER PRIMARY KEY, username TEXT NOT NULL, pass TEXT NOT NULL, salt TEXT NOT NULL)";
private static final String CREATE_ACCOUNTCONTACT_TABLE = "CREATE TABLE IF NOT EXISTS accountContact( uid INTEGER, cid TEXT, PRIMARY KEY(uid, cid), FOREIGN KEY (uid) REFERENCES accounts(uid)," +
                                                            "FOREIGN KEY (cid) REFERENCES contacts(cid))";

private static final String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS chats(uid INTEGER, cid TEXT, PRIMARY KEY(uid,cid), FOREIGN KEY (uid) REFERENCES accounts(uid), " +
                                                            "FOREIGN KEY (cid) REFERENCES contacts(cid))";
private static final String CREATE_CHATMESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS chatMessages(uid INTEGER, cid TEXT, mid INTEGER, PRIMARY KEY(uid,cid,mid), " +
                                                            "FOREIGN KEY (uid, cid) REFERENCES chats(uid,cid), FOREIGN KEY (mid) REFERENCES messages(mid))";

private static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS messages(mid INTEGER PRIMARY KEY, message TEXT NOT NULL, dt INTEGER NOT NULL, status INTEGER NOT NULL)";
private static final String CREATE_RETAIN_TABLE = "CREATE TABLE IF NOT EXISTS retained(uid INTEGER, cid TEXT, datatype INTEGER NOT NULL, PRIMARY KEY(uid, cid), " +
                                                            "FOREIGN KEY (uid) REFERENCES accounts(uid), FOREIGN KEY (cid) REFERENCES contacts(cid))";

private static final String INSERT_CONTACT = "INSERT INTO contacts(cid, alias, ipv4, tlsport) VALUES(?,?,?,?)";
private static final String INSERT_ACCOUNT = "INSERT INTO accounts(uid, username, pass, salt) VALUES(?,?,?,?)";
private static final String INSERT_ACCOUNTCONTACT = "INSERT INTO accountContact(uid, cid) VALUES(?,?)";
private static final String INSERT_CHAT = "INSERT INTO chats(uid, cid) VALUES(?,?)";
private static final String INSERT_CHATMESSAGES = "INSERT INTO chatMessages(uid, cid, mid) VALUES(?,?,?)";
private static final String INSERT_MESSAGE = "INSERT INTO messages(mid, message, dt, status) VALUES(?,?,?,?)";
private static final String INSERT_RETAIN = "INSERT INTO retained(uid, cid, datatype) VALUES(?,?,?)";

private static final String RETRIEVE_MAX_UID = "SELECT COALESCE(MAX(uid), 0) FROM accounts";
private static final String RETRIEVE_MAX_MID = "SELECT COALESCE(MAX(mid), 0) FROM messages";

//public static final String SELECT_MESSAGES = "SELECT * FROM MESSAGES WHERE ";

private PreparedStatement queryInsertContact;
private PreparedStatement queryInsertAccount;
private PreparedStatement queryInsertAccountContact;
private PreparedStatement queryInsertChat;
private PreparedStatement queryInsertChatMessages;
private PreparedStatement queryInsertMessage;
private PreparedStatement queryInsertRetain;

private PreparedStatement queryRetrieveMaxUid;
private PreparedStatement queryRetrieveMaxMid;

private int accountCounter;
private int messageCounter;

private  DatabaseUtilites(){
        openConnection();
        setupPreparedStatements();
        setupCounters();

}

/**
Returns the singleton DatabaseUtilites instance
 */
public static DatabaseUtilites getInstance(){
    return databaseUtilites;
}

/**
Opens the database connection and then calls setupDatabase which creates the tables if they do not already exist

 */
private void openConnection(){

    try {
        conn = DriverManager.getConnection(CONNECTION_STRING);
        if(conn != null) {
            setupDatabase();
        }
    }catch(SQLException e){
        System.out.println("Failed to open connection: " + e.getMessage());
    }
}

/**
sets up tables which do not already exist
 */
private void setupDatabase(){

    try{
        Statement statement = conn.createStatement();

        conn.setAutoCommit(false);
        statement.addBatch(CREATE_CONTACTS_TABLE);
        statement.addBatch(CREATE_ACCOUNTS_TABLE);
        statement.addBatch(CREATE_ACCOUNTCONTACT_TABLE);
        statement.addBatch(CREATE_CHAT_TABLE);
        statement.addBatch(CREATE_CHATMESSAGES_TABLE);
        statement.addBatch(CREATE_MESSAGES_TABLE);
        statement.addBatch(CREATE_RETAIN_TABLE);

        statement.executeBatch();
        conn.commit();
        conn.setAutoCommit(true);

    }catch(SQLException e){
        System.out.println("Failed to setup database: " + e.getMessage());
    }
}


/**
Setup query prepared statements

 */
private void setupPreparedStatements(){

    try{
        queryInsertContact = conn.prepareStatement(INSERT_CONTACT);
        queryInsertAccount = conn.prepareStatement(INSERT_ACCOUNT);
        queryInsertAccountContact = conn.prepareStatement(INSERT_ACCOUNTCONTACT);
        queryInsertChat = conn.prepareStatement(INSERT_CHAT);
        queryInsertChatMessages = conn.prepareStatement(INSERT_CHATMESSAGES);
        queryInsertMessage = conn.prepareStatement(INSERT_MESSAGE);
        queryInsertRetain = conn.prepareStatement(INSERT_RETAIN);

        queryRetrieveMaxUid = conn.prepareStatement(RETRIEVE_MAX_UID);
        queryRetrieveMaxMid = conn.prepareStatement(RETRIEVE_MAX_MID);

    } catch (SQLException e){// !!! ADDD NEW METHOD FOR INSERTING RETAINED DATA

        System.out.println("Failed to setup prepared statements: " + e.getMessage());
    }

}

/**
 * Sets up the counters for both both the messages and account tables on their ids
 */
private void setupCounters(){

    try{
        ResultSet result;
        result = queryRetrieveMaxUid.executeQuery();
        if(result.next())
            accountCounter = result.getInt(1) + 1;

        result = queryRetrieveMaxMid.executeQuery();
        if(result.next())
            messageCounter = result.getInt(1) + 1;

    }catch(SQLException e){
        System.out.println("Failed to setup counters: " + e.getMessage());
    }
}

/**
Closes the connection when the application closes
 */
private void closeConnection(){

    try{
        if(queryInsertContact != null){
            queryInsertContact.close();
        }
        if(queryInsertAccount != null){
            queryInsertAccount.close();
        }
        if(queryInsertAccountContact != null){
            queryInsertAccountContact.close();
        }
        if(queryInsertChat != null){
            queryInsertChat.close();
        }
        if(queryInsertChatMessages != null){
            queryInsertChatMessages.close();
        }
        if(queryInsertMessage != null){
            queryInsertMessage.close();
        }
        if(queryInsertRetain != null){
            queryInsertRetain.close();
        }
        if(queryRetrieveMaxUid != null){
            queryRetrieveMaxUid.close();
        }
        if(queryRetrieveMaxMid != null){
            queryRetrieveMaxMid.close();
        }
        if(conn != null){
            conn.close();
        }
    }catch (SQLException e){
        System.out.println("Failed to close connection: " + e.getMessage());
    }

}

    /**
    Add a contact to the database if the alias is not greater than 255 characters and the ipv4 variable matches
    the ipv4 pattern matcher
    */
    public boolean addContact(String cid, String alias, String ipv4, int tlsport){

    if(ipv4Pattern.matcher(ipv4).matches() && alias.length() <= 256 && tlsport >= 0 && tlsport <= 65535){

        try {
            queryInsertContact.setString(1,cid);
            queryInsertContact.setString(2, alias);
            queryInsertContact.setString(3, ipv4);
            queryInsertContact.setInt(4, tlsport);
            queryInsertContact.executeUpdate();

            return true;

        }catch(SQLException e){
            System.out.println("Failed to add contact: " + e.getMessage());
        }
    }

    return false;
}

    /**addAccountContact
     * Add an account to the database if the username, password and salt are within test bounds
     * @param username the accounts identifier
     * @param pass the accounts password
     * @param salt the salt for the password
     * @return return true if successful and false if failed
     */
    public boolean addAccount(String username, String pass, String salt, String cid, String alias, String ipv4, int tlsport){

    if(username.length() <= 256 && pass.length() <= 256 && salt.length() == 88){
     try {
         int uid = accountCounter++;

         // !!! ROLLBACK INSTEAD OF IF
             queryInsertAccount.setInt(1, uid);
             queryInsertAccount.setString(2, username);
             queryInsertAccount.setString(3, pass);
             queryInsertAccount.setString(4, salt);
             queryInsertAccount.executeUpdate();

             if(addContact(cid, alias, ipv4, tlsport)) {
                 if(addAccountContact(uid, cid))
                 return true;
             }

     }catch(SQLException e){
         System.out.println("Failed to add account: "+ e.getMessage());
     }
    }

    return false;
}


// !!! ADD A TEST FOR THIS METHOD
        /**
         * Adds a record to accountContact which links a new account to its contact record
         * @param uid the account table primary key
         * @param cid the contact table primary key
         * @return true if successful
         */
    private boolean addAccountContact(int uid, String cid){

        try {
            queryInsertAccountContact.setInt(1, uid);
            queryInsertAccountContact.setString(2, cid);
            if(queryInsertAccountContact.executeUpdate() > 0)
            return true;

        }catch(SQLException e){
            System.out.println("Failed to add account contact link " + e.getMessage());
        }

        return false;
    }




    /**
     * Add a chat record to the database for the relevant account and contact
     * @param uid the account id
     * @param cid the contact id
     * @return true if the operation is successful and false if not
     */
    public boolean addChat( int uid, String cid){

        // COULD VALIDATE THE INPUTS HERE
        try{
            queryInsertChat.setInt(1, uid);
            queryInsertChat.setString(2, cid);
            if(queryInsertChat.executeUpdate() > 0)
            return true;

        } catch(SQLException e){
            System.out.println("Failed to add chat: " + e.getMessage());
        }
    return false;
}

        /**
         * Adds a link between the chat table and new messages added to the database
         * @param uid account identifier is part of the chat composite pk
         * @param cid contact identifier is part of the chat composite pk
         * @param mid message identifer is the primary key of the messages table
         * @return true is returned on success
         */
    private boolean addChatMessages(int uid, String cid, int mid){

        try{
            queryInsertChatMessages.setInt(1, uid);
            queryInsertChatMessages.setString(2, cid);
            queryInsertChatMessages.setInt(3, mid);
            if(queryInsertChatMessages.executeUpdate() > 0)
            return true;

        }catch(SQLException e){
            System.out.println("Failed to add chat message record: " + e.getMessage());
        }

        return false;

    }


    /**
     * Add a message record to the database
     * @param cid the contact id
     * @param uid the account id
     * @param message the message string encrypted with the accounts public key
     * @param dt the datetime string
     * @param status the status enum to determine if the message was sent, recieved or pending successful transmission
     * @return true if the operation is successful and false if not
     */
     boolean addMessage(String cid,int uid, String message, long dt, MessageStatus status){

         // !!! WOULD BE BETTER WITH BATCH IF POSSIBLE
         // add a message size limit of 255 chars

         try {
            int mid = messageCounter++;

                queryInsertMessage.setInt(1,mid);
                queryInsertMessage.setString(2,message);
                queryInsertMessage.setLong(3, dt);
                queryInsertMessage.setInt(4,status.getCode());
                if(queryInsertMessage.executeUpdate() > 0) {
                    if (addChatMessages(uid, cid, mid))
                        return true;
                }

        }catch(SQLException e){
            System.out.println("Failed to add message: " + e.getMessage());
    }
    return false;
}


    boolean addRetained(int uid, String cid, RetainedDatatype datatype){

         try{
             queryInsertRetain.setInt(1, uid);
             queryInsertRetain.setString(2, cid);
             queryInsertRetain.setInt(3, datatype.getCode());
             if(queryInsertRetain.executeUpdate() >0)
             return true;

         }catch(SQLException e){
             System.out.println("Failed to add retained record: " + e.getMessage());
         }

         return false;
    }



// A TEMPORARY METHOD FOR TESTING PURPOSES
public boolean tempMethod(){

        try {
            Statement statement = conn.createStatement();
            statement.execute("DELETE FROM contacts");
            statement.execute("DELETE FROM accounts");
            statement.execute("DELETE FROM accountContact");
            statement.execute("DELETE FROM chats");
            statement.execute("DELETE FROM chatMessages");
            statement.execute("DELETE FROM messages");
            statement.execute("DELETE FROM retained");

            return true;
        }catch(SQLException e){
            System.out.println(e.getMessage());
}
return false;
}

}
