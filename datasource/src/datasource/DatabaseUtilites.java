package datasource;

import java.io.File;
import java.sql.*;
import java.util.regex.Pattern;

    public class DatabaseUtilites {

private static DatabaseUtilites databaseUtilites = new DatabaseUtilites();
private Connection conn;
private Pattern ipv4Pattern = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
private Pattern dateTime = Pattern.compile("^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}$");

public String DB_NAME = "secure-messenger.db";
// connection string could not be psf because concatenation with File.separator renders the string null
public String CONNECTION_STRING = "jdbc:sqlite:" + "resources" + File.separator + DB_NAME;


public static final String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS contacts(cid INTEGER PRIMARY KEY, cnum INTEGER NOT NULL UNIQUE, username VARCHAR(255), ipv4 CHAR(15), port INTEGER)";
public static final String CREATE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS accounts(uid INTEGER PRIMARY KEY, cid INTEGER NOT NULL , username VARCHAR(255), pass varchar(255), salt char(88)," +
                                                   "FOREIGN KEY (cid) REFERENCES contacts(cid))";
public static final String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS chats(chid INTEGER PRIMARY KEY, cnum INTEGER, uid INTEGER, FOREIGN KEY (cnum) REFERENCES contacts(cnum)," +
                                                "FOREIGN KEY (uid) REFERENCES accounts(uid))";
public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS messages(mid INTEGER PRIMARY KEY, chid INTEGER, message TEXT, dt TEXT, status TEXT, FOREIGN KEY (chid) REFERENCES contacts(chid))";


public static final String INSERT_CONTACT = "INSERT INTO contacts(cnum,username,ipv4, port) VALUES(? ,?, ?, ?)";
public static final String INSERT_ACCOUNT = "INSERT INTO accounts(cid, username, pass, salt) VALUES (?,?,?,?)";
public static final String INSERT_CHAT = "INSERT INTO chats( cnum, uid) VALUES(?, ?)";
public static final String INSERT_MESSAGE = "INSERT INTO messages(chid, message, dt, status) VALUES(?,?,?,?)";

public static final String RETRIEVE_CID = "SELECT last_insert_rowid()";
public static final String RETRIEVE_CHAT = "SELECT chid FROM chats WHERE cnum = ? AND uid = ?";

//public static final String SELECT_MESSAGES = "SELECT * FROM MESSAGES WHERE ";

private PreparedStatement queryInsertContact;
private PreparedStatement queryInsertAccount;
private PreparedStatement queryInsertChat;
private PreparedStatement queryInsertMessage;

private PreparedStatement queryRetrieveCid;
private PreparedStatement queryConfirmCidUid;

private  DatabaseUtilites(){
        openConnection();
        setupPreparedStatements();
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
        statement.addBatch(CREATE_CHAT_TABLE);
        statement.addBatch(CREATE_MESSAGES_TABLE);

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
        queryInsertChat = conn.prepareStatement(INSERT_CHAT);
        queryInsertMessage = conn.prepareStatement(INSERT_MESSAGE);

        queryRetrieveCid = conn.prepareStatement(RETRIEVE_CID);
        queryConfirmCidUid = conn.prepareStatement(RETRIEVE_CHAT);

    } catch (SQLException e){
        System.out.println("Failed to setup prepared statements: " + e.getMessage());
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
        if(queryInsertChat != null){
            queryInsertChat.close();
        }
        if(queryInsertMessage != null){
            queryInsertMessage.close();
        }
        if(queryRetrieveCid != null){
            queryRetrieveCid.close();
        }
        if(queryConfirmCidUid != null){
            queryConfirmCidUid.close();
        }
        if(conn != null){
            conn.close();
        }
    }catch (SQLException e){
        System.out.println("Failed to close connection: " + e.getMessage());
    }

}

/**
Add a contact to the database if the username is not greater than 255 characters and the ipv4 variable matches
the ipv4 pattern matcher
 */
public boolean addContact(int cnum, String username, String ipv4, int port){

    if(ipv4Pattern.matcher(ipv4).matches() && username.length() <= 255){

        try {
            queryInsertContact.setInt(1,cnum);
            queryInsertContact.setString(2, username);
            queryInsertContact.setString(3, ipv4);
            queryInsertContact.setInt(4, port);
            queryInsertContact.execute();
            return true;

        }catch(SQLException e){
            System.out.println("Failed to add contact: " + e.getMessage());
        }
    }

    return false;
}

    /**
     * Add an account to the database if the username, password and salt are within test bounds
     * @param username the accounts identifier
     * @param pass the accounts password
     * @param salt the salt for the password
     * @return return true if successful and false if failed
     */
    public boolean addAccount(String username, String pass, String salt){


    if(username.length() <= 255 && pass.length() <= 255 && salt.length() == 88){
     try {
         // try and find a better way to do this
         ResultSet result = queryRetrieveCid.executeQuery();
         result.next();

             queryInsertAccount.setInt(1, result.getInt(1));
             queryInsertAccount.setString(2, username);
             queryInsertAccount.setString(3, pass);
             queryInsertAccount.setString(4, salt);
             queryInsertAccount.execute();

             return true;

     }catch(SQLException e){
         System.out.println("Failed to add account: "+ e.getMessage());
     }
    }

    return false;
}

    /**
     * Add a chat record to the database for the relevant account and contact
     * @param cnum the contact id
     * @param uid the account id
     * @return true if the operation is successful and false if not
     */
    public boolean addChat( int cnum, int uid){

        // COULD VALIDATE THE INPUTS HERE
        try{
            queryInsertChat.setInt(1, cnum);
            queryInsertChat.setInt(2,uid);
            queryInsertChat.execute();

            return true;

        } catch(SQLException e){
            System.out.println("Failed to add chat: " + e.getMessage());
        }
    return false;
}

    /**
     * Add a message record to the database after ccnfirming that a chat record exits for the account and contact
     * @param cnum the contact id
     * @param uid the account id
     * @param message the message string encrypted with the accounts public key
     * @param dt the datetime string
     * @param status the status enum to determine if the message was sent, recieved or pending successful transmission
     * @return true if the operation is successful and false if not
     */
    public boolean addMessage(int cnum,int uid, String message, String dt, MessageStatus status){

    if(dateTime.matcher(dt).matches()){

        try {
            queryConfirmCidUid.setInt(1, cnum);
            queryConfirmCidUid.setInt(2, uid);
            ResultSet result = queryConfirmCidUid.executeQuery();

            if(result.next()){
                queryInsertMessage.setInt(1, result.getInt(1));
                queryInsertMessage.setString(2,message);
                queryInsertMessage.setString(3, dt);
                queryInsertMessage.setString(4,status.toString());
                queryInsertMessage.execute();
                return true;
            }

        }catch(SQLException e){
            System.out.println("Failed to add message: " + e.getMessage());
        }
    }
    return false;
}


// A TEMPORARY METHOD FOR TESTING PURPOSES
public boolean tempMethod(){

        try {
            Statement statement = conn.createStatement();
            statement.execute("DELETE FROM contacts");
            statement.execute("DELETE FROM accounts");
            statement.execute("DELETE FROM chats");
            statement.execute("DELETE FROM messages");
            return true;
        }catch(SQLException e){
            System.out.println(e.getMessage());
}
return false;
}

}
