package common;

import datasource.DatabaseUtilites;
import networking.NetworkingUtilities;

public class Register {

    private DatabaseUtilites databaseUtilites;

    public Register(){
        databaseUtilites = DatabaseUtilites.getInstance();
    }

    public boolean register(String username, String password){
        registerAccount(username, password);


        return false;
    }

    private boolean registerAccount(String username, String password){
        databaseUtilites.addContact(username, NetworkingUtilities.getInetAddress());
//        databaseUtilites.addAccount(username, )

        return false;
    }


    private boolean setupKeyStore(){


        return false;
    }
}
