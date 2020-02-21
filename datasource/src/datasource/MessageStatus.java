package datasource;

import java.util.HashMap;
import java.util.Map;

public enum MessageStatus {
    PENDING(0),
    SENT(1),
    RECIEVED(2);

    private final int code;
    private static Map<Integer, MessageStatus> map = new HashMap<>();

    MessageStatus(int code){
        this.code = code;
    }

    static {
        for(MessageStatus messageStatus : MessageStatus.values()){
            map.put(messageStatus.code, messageStatus);
        }
    }

    public static MessageStatus valueOf(int code){
        return map.get(code);
    }

    public int getCode(){
        return code;
    }
}
