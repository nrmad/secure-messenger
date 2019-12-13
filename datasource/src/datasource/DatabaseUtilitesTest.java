package datasource;

import static org.junit.Assert.*;

public class DatabaseUtilitesTest {


    private DatabaseUtilites databaseUtilites = DatabaseUtilites.getInstance();

    @org.junit.Test
    public void getInstance() {
        assertEquals(databaseUtilites, DatabaseUtilites.getInstance());
    }

    @org.junit.Test
    public void addContact() {
        assertTrue(databaseUtilites.addContact("nrmad","127.0.0.1"));
        assertFalse(databaseUtilites.addContact("azt4er", "127-0-0-1"));
        assertFalse(databaseUtilites.addContact("benny", "1234.0.0.1"));
        assertFalse(databaseUtilites.addContact("azt4er", "127.a.a.a"));

    }

    @org.junit.Test
    public void addAccount() {
        fail();
    }

    @org.junit.Test
    public void addMessage() {
        fail();
    }
}