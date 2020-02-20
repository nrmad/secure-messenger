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

    public static byte[] stringtoPaddedBinary(String convert, int bits) {

        int tempVal;

        if ((tempVal = Integer.valueOf(convert)) < (bits^16)) {
            return String.format("%" + bits + "s", Integer.toBinaryString(tempVal)).replace(" ", "0").getBytes();
        }
        return null;
    }

}
