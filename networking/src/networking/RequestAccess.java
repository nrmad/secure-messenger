package networking;

import java.net.InetAddress;
import java.security.PublicKey;

public class RequestAccess implements Runnable {

    private InetAddress disparateHost;
    private InetAddress localHost;
    private String username;
    private PublicKey publicKey;

    public RequestAccess(InetAddress disparateHost, InetAddress localHost, String username, PublicKey publicKey){
        this.disparateHost = disparateHost;
        this.localHost = localHost;
        this.username = username;
        this.publicKey = publicKey;
    }

   @Override
   public void run(){

    byte[] packet;



   }

}
