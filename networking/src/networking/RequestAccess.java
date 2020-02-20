package networking;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.PublicKey;

public class RequestAccess implements Runnable {

    private InetAddress disparateHost;
    private InetAddress localHost;
    private String username;
    private PublicKey publicKey;
    private int port;

    public RequestAccess(InetAddress disparateHost, InetAddress localHost, String username, PublicKey publicKey, int port){
        this.disparateHost = disparateHost;
        this.localHost = localHost;
        this.username = username;
        this.publicKey = publicKey;
        this.port = port;
    }

   @Override
   public void run(){

    byte[] packet;
    PacketType enu = PacketType.ENU;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream w = new DataOutputStream(baos);

       try {

           byte[] temp;

           w.writeInt(enu.getCode());
           w.write(localHost.getAddress());
           if((temp = NetworkingUtilities.stringtoPaddedBinary(Integer.toString(port), 16)) != null){
           w.write(temp);
        //   w.write()

           w.flush();
           packet = baos.toByteArray();


           }


       } catch(IOException e){
           System.out.println("failed to create packet: "+ e.getMessage());
       }


   }

}
