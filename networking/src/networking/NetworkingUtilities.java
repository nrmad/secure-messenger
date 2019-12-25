package networking;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkingUtilities {


    public static InetAddress getInetAddress(){
        try {
            return InetAddress.getLocalHost();

        }catch(UnknownHostException e){
            System.out.println("Cannot obtain local host address" + e.getMessage());
        }
        return null;
    }



}
