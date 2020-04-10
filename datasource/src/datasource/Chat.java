package datasource;

public class Chat {

    private int uid;
    private String cid;

    public Chat(int uid, String cid) {
        this.uid = uid;
        this.cid = cid;
    }

    public int getUid() {
        return uid;
    }

    public String getCid() {
        return cid;
    }
}
