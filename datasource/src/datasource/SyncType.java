package datasource;

import java.util.HashMap;
import java.util.Map;

public enum SyncType {

    CONTACT(0),
    IP(1),
    TLSPORT(2);

    private final int code;
    private static Map<Integer, SyncType> map = new HashMap<>();

    SyncType(int code){
        this.code = code;
    }

    static {
        for(SyncType syncType : SyncType.values()){
            map.put(syncType.code, syncType);
        }
    }

    public static SyncType valueOf(int code){
        return map.get(code);
    }

    public int getCode(){
        return code;
    }

}
