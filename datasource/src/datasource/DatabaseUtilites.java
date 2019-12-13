package datasource;

import java.io.File;
import java.sql.*;
import java.util.regex.Pattern;

public class DatabaseUtilites {

private static DatabaseUtilites databaseUtilites = new DatabaseUtilites();
private Connection conn;
private Statement statement;
private Pattern ipv4Pattern = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");

public String DB_NAME = "secure-messenger.db";
// connection string could not be psf because concatenation with File.separator renders the string null
public String CONNECTION_STRING = "jdbc:sqlite:" + "resources" + File.separator + DB_NAME;


public static final String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS contacts(cid INTEGER PRIMARY KEY, username VARCHAR(255), ipv4 CHAR(15))";
public static final String CREATE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS accounts(uid INTEGER PRIMARY KEY, cid INTEGER , username VARCHAR(255), pass varchar(255), salt char(64)," +
                                                   "FOREIGN KEY (cid) REFERENCES contacts(cid))";
public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS messages(mid INTEGER, cid INTEGER, uid INTEGER, messages TEXT, dt TEXT, FOREIGN KEY (cid) REFERENCES contacts(cid)," +
                                                    "FOREIGN KEY (uid) REFERENCES accounts(uid), PRIMARY KEY(mid, cid, uid))";

public static final String INSERT_CONTACT = "INSERT INTO contacts(username, ipv4) VALUES(?, ?)";
public static final String INSERT_ACCOUNT = "INSERT INTO accounts(cid, username, pass, salt) VALUES (?,?,?,?)";
public static final String INSERT_MESSAGE = "INSERT INTO messages(mid, cid, uid, messages, dt) VALUES(?,?,?,?,?) ";

//public static final String SELECT_MESSAGES = "SELECT * FROM MESSAGES WHERE "

private PreparedStatement queryInsertContact;
private PreparedStatement queryInsertAccount;
private PreparedStatement queryInsertMessage;

private  DatabaseUtilites(){
        openConnection();
        setupPreparedStatements();
}

/*
Returns the singleton DatabaseUtilites instance
 */
public static DatabaseUtilites getInstance(){
    return databaseUtilites;
}

/*
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

/*
sets up tables which do not already exist
 */
private void setupDatabase(){

    try{
        statement = conn.createStatement();

        conn.setAutoCommit(false);
        statement.addBatch(CREATE_CONTACTS_TABLE);
        statement.addBatch(CREATE_ACCOUNTS_TABLE);
        statement.addBatch(CREATE_MESSAGES_TABLE);

        statement.executeBatch();
        conn.commit();
        conn.setAutoCommit(true);

    }catch(SQLException e){
        System.out.println("Failed to setup database: " + e.getMessage());
    }
}


/*
Setup query prepared statements
 */
private void setupPreparedStatements(){

    try{
        queryInsertContact = conn.prepareStatement(INSERT_CONTACT);
        queryInsertAccount = conn.prepareStatement(INSERT_ACCOUNT);
        queryInsertMessage = conn.prepareStatement(INSERT_MESSAGE);


    } catch (SQLException e){
        System.out.println("Failed to setup prepared statements: " + e.getMessage());
    }

}

/*
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
        if(queryInsertMessage != null){
            queryInsertMessage.close();
        }
        if(conn != null){
            conn.close();
        }
    }catch (SQLException e){
        System.out.println("Failed to close connection: " + e.getMessage());
    }

}

/*
Add a contact to the database if the username is not greater than 255 characters and the ipv4 variable matches
the ipv4 pattern matcher
 */
public boolean addContact(String username, String ipv4){

    if(ipv4Pattern.matcher(ipv4).matches() && username.length() <= 255){

        try {
            queryInsertContact.setString(1, username);
            queryInsertContact.setString(2, ipv4);
            queryInsertContact.execute();
            return true;

        }catch(SQLException e){
            System.out.println("Failed to add contact: " + e.getMessage());
        }
    }

    return false;
}


public boolean addAccount(){

    return true;
}

public boolean addMessage(){

    return true;
}


}
