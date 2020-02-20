package networking;

public enum PacketType {

    ENU(1),
    CNU(2);

    private final int code;

    PacketType(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

}
