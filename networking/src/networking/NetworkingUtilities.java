package networking;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkingUtilities {


    public static String getInetAddress(){
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();

        }catch(UnknownHostException e){
            System.out.println("Cannot obtain local host address" + e.getMessage());
        }
        return null;
    }

}
