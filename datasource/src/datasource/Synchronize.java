package datasource;

public class Synchronize {
    private int uid;
    private String cid;
    private SyncType syncType;

    public Synchronize(int uid, String cid, SyncType syncType) {
        this.uid = uid;
        this.cid = cid;
        this.syncType = syncType;
    }

    public int getUid() {
        return uid;
    }

    public String getCid() {
        return cid;
    }

    public SyncType getSyncType() {
        return syncType;
    }
}
