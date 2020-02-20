package networking;

import java.net.InetAddress;
import java.security.PublicKey;

public class Sender {

public static boolean requestAccess(InetAddress disparateHost, PublicKey publicKey, String username, int port){

    InetAddress localHost = NetworkingUtilities.getInetAddress();

    if(localHost != null) {

        Thread requestAccess = new Thread(new RequestAccess(disparateHost, localHost, username, publicKey, port));
        requestAccess.start();
        return true;

    }

    return false;
}

}
