package datasource;

import java.util.HashMap;
import java.util.Map;

public enum RetainedDatatype {

    CONTACT(0),
    IP(1),
    TLSPORT(2);

    private final int code;
    private static Map<Integer, RetainedDatatype> map = new HashMap<>();

    RetainedDatatype(int code){
        this.code = code;
    }

    static {
        for(RetainedDatatype retainedDatatype : RetainedDatatype.values()){
            map.put(retainedDatatype.code, retainedDatatype);
        }
    }

    public static RetainedDatatype valueOf(int code){
        return map.get(code);
    }

    public int getCode(){
        return code;
    }

}
