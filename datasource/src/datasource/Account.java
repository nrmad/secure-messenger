package datasource;

public class Account {

    private String username;
    private String key;
    private String salt;
    private int iterations;

    public Account(String username, String key, String salt, int iterations) {
        this.username = username;
        this.key = key;
        this.salt = salt;
        this.iterations = iterations;
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
