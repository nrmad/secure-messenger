package datasource;

public enum RetainedDatatype {

    CONTACT(0),
    IP(1),
    TLSPORT(2);

    private final int code;

    RetainedDatatype(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }


}
