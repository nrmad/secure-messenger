package datasource;

public class Account {

    private int uid;
    private String username;
    private String key;
    private String salt;
    private int iterations;

    /**
     * Constructor for Account object with -1 uid denoting the lack of an index
     * @param username the account username
     * @param key the account key
     * @param salt the account salt
     * @param iterations the account iteration count
     */
    public Account(String username, String key, String salt, int iterations){
        this(-1, username, key, salt, iterations);
    }

    public Account(int uid, String username, String key, String salt, int iterations) {
        this.uid = uid;
        this.username = username;
        this.key = key;
        this.salt = salt;
        this.iterations = iterations;
    }

    public int getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getKey() {
        return key;
    }

    public String getSalt() {
        return salt;
    }

    public int getIterations() {
        return iterations;
    }
}
