package datasource;

public enum MessageStatus {
    PENDING(0),
    SENT(1),
    RECIEVED(2);

    private final int code;

    MessageStatus(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
