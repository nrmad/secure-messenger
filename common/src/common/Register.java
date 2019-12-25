package common;

import datasource.DatabaseUtilites;
import encryption.EncryptionUtilities;
import networking.Sender;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;

public class Register {

    private DatabaseUtilites databaseUtilites;

    public Register(){
        databaseUtilites = DatabaseUtilites.getInstance();
    }

    public boolean register(String username, String password, String ip){
        KeyPair keyPair = EncryptionUtilities.generateKeyPair();

        if(keyPair != null){
            try {
                InetAddress inetAddress = InetAddress.getByName(ip);
                // make request of disparate host
                if(Sender.requestAccess(inetAddress, keyPair.getPublic(), username)){



                }
            } catch(UnknownHostException e){
                System.out.println("IP address format incorrect: "+ e.getMessage());
            }
        }

        return false;
    }

    private boolean registerAccount(String username, String password){
//        databaseUtilites.addContact(username, NetworkingUtilities.getInetAddress());
//        databaseUtilites.addAccount(username, )

        return false;
    }



}
