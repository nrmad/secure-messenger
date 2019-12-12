package datasource;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtilites {

private static DatabaseUtilites databaseUtilites = new DatabaseUtilites();
private Connection conn;

private  DatabaseUtilites(){
        openConnection();
}

public static DatabaseUtilites getInstance(){
    return databaseUtilites;
}

private boolean openConnection(){

    try {
        conn = DriverManager.getConnection("jdbc:sqlite:" + "resources" + File.separator + "secure-messenger.db", "user", "pass");

    }catch(SQLException e){
        System.out.println(e.getMessage());
        return false;
    }

    return true;
}



}
