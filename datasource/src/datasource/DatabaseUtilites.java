package datasource;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.regex.Pattern;

public class DatabaseUtilites {

    private static DatabaseUtilites databaseUtilites = new DatabaseUtilites();
    private Connection conn;
    private Pattern ipv4Pattern = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
//private Pattern dateTime = Pattern.compile("^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}$");

    public String DB_NAME = "secure-messenger.db";
    // connection string could not be psf because concatenation with File.separator renders the string null
    public String CONNECTION_STRING = "jdbc:sqlite:" + "resources" + File.separator + DB_NAME;

    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS contacts(cid TEXT PRIMARY KEY, alias TEXT NOT NULL, ipv4 TEXT NOT NULL, tlsport INTEGER NOT NULL)";
    private static final String CREATE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS accounts(uid INTEGER PRIMARY KEY, username TEXT NOT NULL, pass TEXT NOT NULL, salt TEXT NOT NULL)";
    private static final String CREATE_ACCOUNTCONTACT_TABLE = "CREATE TABLE IF NOT EXISTS accountContact( uid INTEGER, cid TEXT, PRIMARY KEY(uid, cid), FOREIGN KEY (uid) REFERENCES accounts(uid)," +
            "FOREIGN KEY (cid) REFERENCES contacts(cid))";

    private static final String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS chats(uid INTEGER, cid TEXT, PRIMARY KEY(uid,cid), FOREIGN KEY (uid) REFERENCES accounts(uid), " +
            "FOREIGN KEY (cid) REFERENCES contacts(cid))";
    private static final String CREATE_CHATMESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS chatMessages(uid INTEGER, cid TEXT, mid INTEGER, PRIMARY KEY(uid,cid,mid), " +
            "FOREIGN KEY (uid, cid) REFERENCES chats(uid,cid), FOREIGN KEY (mid) REFERENCES messages(mid))";

    private static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS messages(mid INTEGER PRIMARY KEY, message TEXT NOT NULL, dt INTEGER NOT NULL, status INTEGER NOT NULL)";
    private static final String CREATE_RETAIN_TABLE = "CREATE TABLE IF NOT EXISTS synchronize(uid INTEGER, cid TEXT, datatype INTEGER NOT NULL, PRIMARY KEY(uid, cid), " +
            "FOREIGN KEY (uid) REFERENCES accounts(uid), FOREIGN KEY (cid) REFERENCES contacts(cid))";

    private static final String INSERT_CONTACT = "INSERT INTO contacts(cid, alias, ipv4, tlsport) VALUES(?,?,?,?)";
    private static final String INSERT_ACCOUNT = "INSERT INTO accounts(uid, username, pass, salt, iterations) VALUES(?,?,?,?,?)";
    private static final String INSERT_ACCOUNTCONTACT = "INSERT INTO accountContact(uid, cid) VALUES(?,?)";
    private static final String INSERT_CHAT = "INSERT INTO chats(uid, cid) VALUES(?,?)";
    private static final String INSERT_CHATMESSAGES = "INSERT INTO chatMessages(uid, cid, mid) VALUES(?,?,?)";
    private static final String INSERT_MESSAGE = "INSERT INTO messages(mid, message, dt, status) VALUES(?,?,?,?)";
    private static final String INSERT_RETAIN = "INSERT INTO synchronize(uid, cid, datatype) VALUES(?,?,?)";

    private static final String RETRIEVE_MAX_UID = "SELECT COALESCE(MAX(uid), 0) FROM accounts";
    private static final String RETRIEVE_MAX_MID = "SELECT COALESCE(MAX(mid), 0) FROM messages";

//public static final String SELECT_MESSAGES = "SELECT * FROM MESSAGES WHERE ";

    private PreparedStatement queryInsertContact;
    private PreparedStatement queryInsertAccount;
    private PreparedStatement queryInsertAccountContact;
    private PreparedStatement queryInsertChat;
    private PreparedStatement queryInsertChatMessages;
    private PreparedStatement queryInsertMessage;
    private PreparedStatement queryInsertSynchronize;

    private PreparedStatement queryRetrieveMaxUid;
    private PreparedStatement queryRetrieveMaxMid;

    private int accountCounter;
    private int messageCounter;

    private DatabaseUtilites() {
        openConnection();
        setupPreparedStatements();
        setupCounters();

    }

    /**
     * Returns the singleton DatabaseUtilites instance
     */
    public static DatabaseUtilites getInstance() {
        return databaseUtilites;
    }

    /**
     * Opens the database connection and then calls setupDatabase which creates the tables if they do not already exist
     */
    private void openConnection() {

        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            if (conn != null) {
                setupDatabase();
            }
        } catch (SQLException e) {
            System.out.println("Failed to open connection: " + e.getMessage());
        }
    }

    /**
     * sets up tables which do not already exist
     */
    private void setupDatabase() {

        try {
            Statement statement = conn.createStatement();

//        conn.setAutoCommit(false);
            statement.addBatch(CREATE_CONTACTS_TABLE);
            statement.addBatch(CREATE_ACCOUNTS_TABLE);
            statement.addBatch(CREATE_ACCOUNTCONTACT_TABLE);
            statement.addBatch(CREATE_CHAT_TABLE);
            statement.addBatch(CREATE_CHATMESSAGES_TABLE);
            statement.addBatch(CREATE_MESSAGES_TABLE);
            statement.addBatch(CREATE_RETAIN_TABLE);

            statement.executeBatch();

//        conn.commit();
//        conn.setAutoCommit(true);

        } catch (SQLException e) {
            System.out.println("Failed to setup database: " + e.getMessage());
        }
    }


    /**
     * Setup query prepared statements
     */
    private void setupPreparedStatements() {

        try {
            queryInsertContact = conn.prepareStatement(INSERT_CONTACT);
            queryInsertAccount = conn.prepareStatement(INSERT_ACCOUNT);
            queryInsertAccountContact = conn.prepareStatement(INSERT_ACCOUNTCONTACT);
            queryInsertChat = conn.prepareStatement(INSERT_CHAT);
            queryInsertChatMessages = conn.prepareStatement(INSERT_CHATMESSAGES);
            queryInsertMessage = conn.prepareStatement(INSERT_MESSAGE);
            queryInsertSynchronize = conn.prepareStatement(INSERT_RETAIN);

            queryRetrieveMaxUid = conn.prepareStatement(RETRIEVE_MAX_UID);
            queryRetrieveMaxMid = conn.prepareStatement(RETRIEVE_MAX_MID);

        } catch (SQLException e) {

            System.out.println("Failed to setup prepared statements: " + e.getMessage());
        }

    }

    /**
     * Sets up the counters for both both the messages and account tables on their ids
     */
    private void setupCounters() {

        try {
            ResultSet result;
            result = queryRetrieveMaxUid.executeQuery();
            if (result.next())
                accountCounter = result.getInt(1) + 1;

            result = queryRetrieveMaxMid.executeQuery();
            if (result.next())
                messageCounter = result.getInt(1) + 1;

        } catch (SQLException e) {
            System.out.println("Failed to setup counters: " + e.getMessage());
        }
    }

    /**
     * Closes the connection when the application closes
     */
    private void closeConnection() {

        try {
            if (queryInsertContact != null) {
                queryInsertContact.close();
            }
            if (queryInsertAccount != null) {
                queryInsertAccount.close();
            }
            if (queryInsertAccountContact != null) {
                queryInsertAccountContact.close();
            }
            if (queryInsertChat != null) {
                queryInsertSynchronize.close();
            }
            if (queryInsertChat != null) {
                queryInsertChat.close();
            }
            if (queryInsertChatMessages != null) {
                queryInsertChatMessages.close();
            }
            if (queryInsertMessage != null) {
                queryInsertMessage.close();
            }
            if (queryInsertSynchronize != null) {
                queryInsertSynchronize.close();
            }
            if (queryRetrieveMaxUid != null) {
                queryRetrieveMaxUid.close();
            }
            if (queryRetrieveMaxMid != null) {
                queryRetrieveMaxMid.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to close connection: " + e.getMessage());
        }

    }

    boolean addContacts(List<Contact> contacts) {
        try {
            try {
                conn.setAutoCommit(false);

                for (Contact contact : contacts) {
                    if (ipv4Pattern.matcher(contact.getIpv4()).matches() && contact.getAlias().length() <= 256 && contact.getTlsPort() >= 1024 && contact.getTlsPort() <= 65535) {
                        queryInsertContact.setString(1, contact.getCid());
                        queryInsertContact.setString(2, contact.getAlias());
                        queryInsertContact.setString(3, contact.getIpv4());
                        queryInsertContact.setInt(4, contact.getTlsPort());
                        queryInsertContact.addBatch();
                    } else {
                        throw new SQLException("Format incorrect");
                    }
                }
                queryInsertContact.executeBatch();
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("failed to add contacts: " + e.getMessage());
            } finally {
                queryInsertContact.clearParameters();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
        }
        return false;
    }

    /**
     * Add a contact to the database if the alias is not greater than 256 characters and the ipv4 variable matches
     * the ipv4 pattern matcher
     */
    private void addContact(Contact contact)
            throws SQLException {
        if (ipv4Pattern.matcher(contact.getIpv4()).matches() && contact.getAlias().length() <= 256 && contact.getTlsPort() >= 1024 && contact.getTlsPort() <= 65535) {
            queryInsertContact.setString(1, contact.getCid());
            queryInsertContact.setString(2, contact.getAlias());
            queryInsertContact.setString(3, contact.getIpv4());
            queryInsertContact.setInt(4, contact.getTlsPort());
            queryInsertContact.executeUpdate();
            queryInsertContact.clearParameters();
        } else {
            throw new SQLException();
        }
    }

    /**
     * addAccountContact
     * Add an account to the database if the username, password and salt are within test bounds
     *
     * @param account an account object
     * @param contact a contact object
     * @return return true if successful and false if failed
     */
    public boolean addAccount(Account account, Contact contact) {

        if (account.getUsername().length() <= 256 && account.getUsername().length() > 0 && account.getKey().length() == 88 && account.getSalt().length() == 88) {

            try {
                try {
                    int uid = accountCounter++;
                    conn.setAutoCommit(false);

                    queryInsertAccount.setInt(1, uid);
                    queryInsertAccount.setString(2, account.getUsername());
                    queryInsertAccount.setString(3, account.getKey());
                    queryInsertAccount.setString(4, account.getSalt());
                    queryInsertAccount.setInt(5, account.getIterations());
                    queryInsertAccount.executeUpdate();
                    queryInsertAccount.clearParameters();
                    addContact(contact);
                    addAccountContact(uid, contact.getCid());
                    conn.commit();
                    return true;

                } catch (SQLException e) {
                    conn.rollback();
                    System.out.println("Failed to add account: " + e.getMessage());
                } finally {
                    conn.setAutoCommit(true);
                    queryInsertAccount.clearParameters();
                }
            } catch (SQLException e) {
            }
        }

        return false;
    }


// !!! ADD A TEST FOR THIS METHOD

    /**
     * Adds a record to accountContact which links a new account to its contact record
     *
     * @param uid the account table primary key
     * @param cid the contact table primary key
     */
    private void addAccountContact(int uid, String cid)
            throws SQLException {
        queryInsertAccountContact.setInt(1, uid);
        queryInsertAccountContact.setString(2, cid);
        queryInsertAccountContact.executeUpdate();
        queryInsertAccountContact.clearParameters();
    }


    /**
     * Add a chat record to the database for the relevant account and contact
     *
     * @param chat a chat object
     * @return true if the operation is successful and false if not
     */
    public boolean addChat(Chat chat) {

        // COULD VALIDATE THE INPUTS HERE
        try {
            queryInsertChat.setInt(1, chat.getUid());
            queryInsertChat.setString(2, chat.getCid());
            queryInsertChat.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Failed to add chat: " + e.getMessage());
        } finally {
            try {
                queryInsertChat.clearParameters();
            } catch (SQLException e) {
            }
        }

        return false;
    }

    /**
     * Adds a link between the chat table and new messages added to the database
     *
     * @param uid account identifier is part of the chat composite pk
     * @param cid contact identifier is part of the chat composite pk
     * @param mid message identifer is the primary key of the messages table
     * @return true is returned on success
     */
    private void addChatMessages(int uid, String cid, int mid)
            throws SQLException {
        queryInsertChatMessages.setInt(1, uid);
        queryInsertChatMessages.setString(2, cid);
        queryInsertChatMessages.setInt(3, mid);
        queryInsertChatMessages.executeUpdate();
        queryInsertChatMessages.clearParameters();

    }


    /**
     * Add a message record to the database
     *
     * @param cid     the contact id
     * @param uid     the account id
     * @param message the message string encrypted with the accounts public key
     * @return true if the operation is successful and false if not
     */
    boolean addMessage(Message message, int uid, String cid) {

        // !!! WOULD BE BETTER WITH BATCH IF POSSIBLE
        // add a message size limit of 255 chars
        try {
            try {
                int mid = messageCounter++;
                conn.setAutoCommit(false);

                queryInsertMessage.setInt(1, mid);
                queryInsertMessage.setString(2, message.getMessage());
                queryInsertMessage.setLong(3, message.getDt());
                queryInsertMessage.setInt(4, message.getStatus().getCode());
                queryInsertMessage.executeUpdate();
                addChatMessages(uid, cid, mid);
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Failed to add message: " + e.getMessage());
            } finally {
                queryInsertMessage.clearParameters();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
        }

        return false;
    }


    boolean addSynchronize(Synchronize synchronize) {

        try {
            queryInsertSynchronize.setInt(1, synchronize.getUid());
            queryInsertSynchronize.setString(2, synchronize.getCid());
            queryInsertSynchronize.setInt(3, synchronize.getSyncType().getCode());
            queryInsertSynchronize.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Failed to add synchronize record: " + e.getMessage());
        } finally {
            try {
                queryInsertSynchronize.clearParameters();
            } catch (SQLException e) {
            }
        }
        return false;
    }


    // A TEMPORARY METHOD FOR TESTING PURPf1dg13d7f8sfdOSES
    public boolean tempMethod() {

        try {
            Statement statement = conn.createStatement();
            statement.execute("DELETE FROM contacts");
            statement.execute("DELETE FROM accounts");
            statement.execute("DELETE FROM accountContact");
            statement.execute("DELETE FROM chats");
            statement.execute("DELETE FROM chatMessages");
            statement.execute("DELETE FROM messages");
            statement.execute("DELETE FROM synchronize");

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
