package datasource;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtilites {

private static DatabaseUtilites databaseUtilites = new DatabaseUtilites();
private Connection conn;
private Statement statement;

public String DB_NAME = "secure-messenger.db";
// connection string could not be psf because concatenation with File.separator renders the string null
public String CONNECTION_STRING = "jdbc:sqlite:" + "resources" + File.separator + DB_NAME;


public static final String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS contacts(cid INTEGER PRIMARY KEY, username VARCHAR(255), ipv4 CHAR(15))";
public static final String CREATE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS accounts(uid INTEGER PRIMARY KEY, cid INTEGER , username VARCHAR(255), pass varchar(255), FOREIGN KEY (cid) REFERENCES contacts(cid)";
public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS messages(mid INTEGER, cid INTEGER, uid, message-text TEXT, date-time TEXT), FOREIGN KEY (cid) REFERENCES contacts(cid)," +
                                                    "FOREIGN KEY (uid) REFERENCES accounts(uid), PRIMARY KEY(mid, cid, uid))";

private  DatabaseUtilites(){
        openConnection();
}

public static DatabaseUtilites getInstance(){
    return databaseUtilites;
}

private void openConnection(){

    try {
        conn = DriverManager.getConnection(CONNECTION_STRING);
        if(conn != null) {
            setupDatabase();
        }
    }catch(SQLException e){
        System.out.println("Failed to open connection: " + e.getMessage());
        //return false;
    }
    //return true;
}

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

private void closeConnection(){

    try{
        if(conn != null){
            conn.close();
        }
    }catch (SQLException e){
        System.out.println("Failed to close connection: " + e.getMessage());
    }

}



}
