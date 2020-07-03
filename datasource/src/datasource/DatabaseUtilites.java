package datasource;

import java.io.File;
import java.sql.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class DatabaseUtilites {

    private static DatabaseUtilites databaseUtilites;
    private Connection conn;
    private Pattern ipv4Pattern = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
//private Pattern dateTime = Pattern.compile("^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}$");

    public String DB_NAME = "secure-messenger.db";
    // connection string could not be psf because concatenation with File.separator renders the string null
    public String CONNECTION_STRING = "jdbc:sqlite:" + "resources" + File.separator + DB_NAME;

    private static final String CREATE_ACCOUNT_CONTACT = "CREATE TABLE IF NOT EXISTS accountContact(aid INTEGER, " +
            "cid INTEGER, PRIMARY KEY(aid,cid), FOREIGN KEY(aid) REFERENCES accounts(aid) ON DELETE CASCADE, " +
            "FOREIGN KEY(cid) REFERENCES contacts(cid) ON DELETE CASCADE)";
    private static final String CREATE_ACCOUNTS = "CREATE TABLE IF NOT EXISTS accounts(aid INTEGER PRIMARY KEY, " +
            "username VARCHAR(255) UNIQUE NOT NULL, password TEXT, salt CHAR(88) NOT NULL, iterations INTEGER NOT NULL)";
    private static final String CREATE_CONTACTS = "CREATE TABLE IF NOT EXISTS contacts(cid INTEGER PRIMARY KEY, " +
            "alias VARCHAR(255) NOT NULL)";
    private static final String CREATE_PORTS = "CREATE TABLE IF NOT EXISTS ports(pid INTEGER PRIMARY KEY, " +
            "port INTEGER UNIQUE NOT NULL)";
    private static final String CREATE_HOSTS = "CREATE TABLE IF NOT EXISTS hosts(hid INTEGER PRIMARY KEY, " +
            "host TEXT UNIQUE NOT NULL)";
    private static final String CREATE_NETWORK_HOSTS = "CREATE TABLE IF NOT EXISTS networkHosts(nid INTEGER, hid INTEGER, " +
            "PRIMARY KEY(nid, hid), FOREIGN KEY(nid) REFERENCES networks(nid), FOREIGN KEY(hid) " +
            "REFERENCES hosts(hid)  ON DELETE CASCADE)";
    private static final String CREATE_NETWORK_PORTS = "CREATE TABLE IF NOT EXISTS networkPorts(nid INTEGER, pid INTEGER, " +
            "PRIMARY KEY(nid, pid), FOREIGN KEY(nid) REFERENCES networks(nid) ON DELETE CASCADE, FOREIGN KEY(pid) " +
            "REFERENCES ports(pid))";
    private static final String CREATE_NETWORKS = "CREATE TABLE IF NOT EXISTS networks(nid INTEGER PRIMARY KEY, " +
            "network_alias VARCHAR(255) UNIQUE NOT NULL)";
    private static final String CREATE_NETWORK_CONTACTS = "CREATE TABLE IF NOT EXISTS networkContacts(nid INTEGER, " +
            "cid INTEGER, PRIMARY KEY(nid,cid), FOREIGN KEY(nid) REFERENCES networks(nid) ON DELETE CASCADE, " +
            "FOREIGN KEY(cid) REFERENCES contacts(cid) ON DELETE CASCADE)";
    private static final String CREATE_ACCOUNT_NETWORK = "CREATE TABLE IF NOT EXISTS accountNetwork(aid INTEGER, " +
            "nid INTEGER, PRIMARY KEY(aid,nid), FOREIGN KEY(aid) REFERENCES accounts(aid) ON DELETE CASCADE, " +
            "FOREIGN KEY(nid) REFERENCES networks(nid) ON DELETE CASCADE)";

    private static final String SELECT_USERNAMES = "SELECT username FROM accounts";
    private static final String INSERT_ACCOUNT = "INSERT INTO accounts(aid, username, password, salt, iterations) " +
            "VALUES (?,?,?,?,?)";
    private static final String INSERT_CONTACT = "INSERT INTO contacts(cid, alias) VALUES(?,?)";
    private static final String INSERT_ACCOUNT_CONTACT = "INSERT INTO accountContact(aid, cid) VALUES (?,?)";
    private static final String INSERT_NETWORK_CONTACTS = "INSERT INTO networkContacts(nid, cid) VALUES (?,?)";
    private static final String INSERT_ACCOUNT_NETWORK = "INSERT INTO accountNetwork(aid, nid) VALUES (?,?)";
    private static final String DELETE_ACCOUNT = "DELETE accounts FROM accounts INNER JOIN accountContact ac ON " +
            "accounts.aid = ac.aid WHERE ac.cid = ?";
    private static final String DELETE_CONTACT = "DELETE FROM contacts WHERE cid = ?";
    private static final String SELECT_ACCOUNT = "SELECT * FROM accounts WHERE username = ? ";
    private static final String SELECT_CONTACT = "SELECT c.cid FROM contacts c INNER JOIN accountContact ac ON c.cid = ac.cid " +
            "INNER JOIN accounts a ON ac.aid = a.aid WHERE a.username = ?";
    private static final String UPDATE_ACCOUNT_CREDENTIALS = "UPDATE accounts SET password = ?, salt = ?, iterations = ?" +
            " WHERE aid = ?";

//private static final String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS chats(uid INTEGER, cid TEXT, PRIMARY KEY(uid,cid), FOREIGN KEY (uid) REFERENCES accounts(uid), " +
//                                                            "FOREIGN KEY (cid) REFERENCES contacts(cid))";
//private static final String CREATE_CHATMESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS chatMessages(uid INTEGER, cid TEXT, mid INTEGER, PRIMARY KEY(uid,cid,mid), " +
//                                                            "FOREIGN KEY (uid, cid) REFERENCES chats(uid,cid), FOREIGN KEY (mid) REFERENCES messages(mid))";
//
//private static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS messages(mid INTEGER PRIMARY KEY, message TEXT NOT NULL, dt INTEGER NOT NULL, status INTEGER NOT NULL)";
//private static final String CREATE_RETAIN_TABLE = "CREATE TABLE IF NOT EXISTS retained(uid INTEGER, cid TEXT, datatype INTEGER NOT NULL, PRIMARY KEY(uid, cid), " +
//                                                            "FOREIGN KEY (uid) REFERENCES accounts(uid), FOREIGN KEY (cid) REFERENCES contacts(cid))";


//private static final String INSERT_CHAT = "INSERT INTO chats(uid, cid) VALUES(?,?)";
//private static final String INSERT_CHATMESSAGES = "INSERT INTO chatMessages(uid, cid, mid) VALUES(?,?,?)";
//private static final String INSERT_MESSAGE = "INSERT INTO messages(mid, message, dt, status) VALUES(?,?,?,?)";
//private static final String INSERT_RETAIN = "INSERT INTO retained(uid, cid, datatype) VALUES(?,?,?)";

    //private static final String RETRIEVE_MAX_UID = "SELECT COALESCE(MAX(uid), 0) FROM accounts";
//private static final String RETRIEVE_MAX_MID = "SELECT COALESCE(MAX(mid), 0) FROM messages";
    private static final String RETRIEVE_MAX_AID = "SELECT COALESCE(MAX(aid), 0) FROM accounts;";

//public static final String SELECT_MESSAGES = "SELECT * FROM MESSAGES WHERE ";

    private PreparedStatement querySelectUsernames;
    private PreparedStatement queryInsertAccount;
    private PreparedStatement queryInsertContact;
    private PreparedStatement queryInsertAccountContact;
    private PreparedStatement queryDeleteContact;
    private PreparedStatement queryInsertNetworkContacts;
    private PreparedStatement queryInsertAccountNetwork;
    private PreparedStatement queryDeleteAccount;
    private PreparedStatement querySelectAccount;
    private PreparedStatement querySelectContact;
    private PreparedStatement queryUpdateAccountCredentials;
//private PreparedStatement queryInsertChat;
//private PreparedStatement queryInsertChatMessages;
//private PreparedStatement queryInsertMessage;
//private PreparedStatement queryInsertRetain;

    private PreparedStatement queryRetrieveMaxAid;
//private PreparedStatement queryRetrieveMaxMid;

    //private int messageCounter;
    private int accountCounter;

    private DatabaseUtilites() throws SQLException {
        openConnection();
        setupPreparedStatements();
        setupCounters();

    }

    /**
     * Returns the singleton DatabaseUtilites instance
     */
    public static DatabaseUtilites getInstance() throws SQLException {
        if (databaseUtilites == null)
            databaseUtilites = new DatabaseUtilites();
        return databaseUtilites;
    }

    /**
     * Opens the database connection and then calls setupDatabase which creates the tables if they do not already exist
     */
    private void openConnection() throws SQLException {

        conn = DriverManager.getConnection(CONNECTION_STRING);
        if (conn != null) {
            setupDatabase();
        }
    }

    /**
     * sets up tables which do not already exist
     */
    private void setupDatabase() {

        try {
            Statement statement = conn.createStatement();

            statement.execute(CREATE_ACCOUNTS);
            statement.execute(CREATE_CONTACTS);
            statement.execute(CREATE_NETWORKS);
            statement.execute(CREATE_PORTS);
            statement.execute(CREATE_HOSTS);
            statement.execute(CREATE_NETWORK_HOSTS);
            statement.execute(CREATE_NETWORK_PORTS);
            statement.execute(CREATE_ACCOUNT_CONTACT);
            statement.execute(CREATE_NETWORK_CONTACTS);
            statement.execute(CREATE_ACCOUNT_NETWORK);


        } catch (SQLException e) {
            System.out.println("Failed to setup database: " + e.getMessage());
        }
    }


    /**
     * Setup query prepared statements
     */
    private void setupPreparedStatements() throws SQLException {


        try {
            querySelectUsernames = conn.prepareStatement(SELECT_USERNAMES);
            queryInsertAccount = conn.prepareStatement(INSERT_ACCOUNT);
            queryInsertContact = conn.prepareStatement(INSERT_CONTACT);
            queryInsertAccountContact = conn.prepareStatement(INSERT_ACCOUNT_CONTACT);
            queryDeleteContact = conn.prepareStatement(DELETE_CONTACT);
            queryInsertNetworkContacts = conn.prepareStatement(INSERT_NETWORK_CONTACTS);
            queryInsertAccountNetwork = conn.prepareStatement(INSERT_ACCOUNT_NETWORK);
            queryDeleteAccount = conn.prepareStatement(DELETE_ACCOUNT);
            querySelectAccount = conn.prepareStatement(SELECT_ACCOUNT);
            querySelectContact = conn.prepareStatement(SELECT_CONTACT);
            queryUpdateAccountCredentials = conn.prepareStatement(UPDATE_ACCOUNT_CREDENTIALS);

//        queryInsertChat = conn.prepareStatement(INSERT_CHAT);
//        queryInsertChatMessages = conn.prepareStatement(INSERT_CHATMESSAGES);
//        queryInsertMessage = conn.prepareStatement(INSERT_MESSAGE);
//        queryInsertRetain = conn.prepareStatement(INSERT_RETAIN);
//
//        queryRetrieveMaxUid = conn.prepareStatement(RETRIEVE_MAX_UID);
//        queryRetrieveMaxMid = conn.prepareStatement(RETRIEVE_MAX_MID);
            queryRetrieveMaxAid = conn.prepareStatement(RETRIEVE_MAX_AID);

        } catch (SQLException e) {// !!! ADDD NEW METHOD FOR INSERTING RETAINED DATA

            System.out.println("Failed to setup prepared statements: " + e.getMessage());
        }

    }

    /**
     * Sets up the counters for both both the messages and account tables on their ids
     */
    private void setupCounters() {

        try {
            ResultSet result;

//        result = queryRetrieveMaxMid.executeQuery();
//        if(result.next())
//            messageCounter = result.getInt(1) + 1;
            result = queryRetrieveMaxAid.executeQuery();
            if (result.next())
                accountCounter = result.getInt(1) + 1;

        } catch (SQLException e) {
            System.out.println("Failed to setup counters: " + e.getMessage());
        }
    }

    /**
     * Closes the connection when the application closes
     */
    private void closeConnection() {

        try {

            if (querySelectUsernames != null) {
                querySelectUsernames.close();
            }
            if (queryInsertAccount != null) {
                queryInsertAccount.close();
            }
            if (queryInsertContact != null) {
                queryInsertContact.close();
            }
            if (queryInsertAccountContact != null) {
                queryInsertAccountContact.close();
            }
            if (queryDeleteContact != null) {
                queryDeleteContact.close();
            }
            if (queryInsertNetworkContacts != null) {
                queryInsertNetworkContacts.close();
            }
            if (queryInsertAccountNetwork != null) {
                queryInsertAccountNetwork.close();
            }
            if (queryDeleteAccount != null) {
                queryDeleteAccount.close();
            }
            if (querySelectAccount != null) {
                querySelectAccount.close();
            }
            if (querySelectContact != null) {
                querySelectContact.close();
            }
            if (queryUpdateAccountCredentials != null) {
                queryUpdateAccountCredentials.close();
            }
//        if(queryInsertChat != null){
//            queryInsertChat.close();
//        }
//        if(queryInsertChatMessages != null){
//            queryInsertChatMessages.close();
//        }
//        if(queryInsertMessage != null){
//            queryInsertMessage.close();
//        }
//        if(queryInsertRetain != null){
//            queryInsertRetain.close();
//        }
//        if(queryRetrieveMaxUid != null){
//            queryRetrieveMaxUid.close();
//        }
//        if(queryRetrieveMaxMid != null){
//            queryRetrieveMaxMid.close();
//        }
            if (queryRetrieveMaxAid != null) {
                queryRetrieveMaxAid.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to close connection: " + e.getMessage());
        }

    }


    /*
    IF ON THIS SIDE CONTACTS ARE CONNECTED TO NETWORKS AND ACCOUNTS ARE CONNECTED TO NETWORKS
    THEN ACCOUNTS NEED ONLY BE CONNECTED DIRECTLY TO THE CONTACT WHICH THEY PERTAIN TO THE REST
    CAN BE LOADED THROUGH THOSE CONNECTED TO THE ACCOUNTS NETWORK
     */
    public Account getAccount(Account account) throws SQLException {
        querySelectAccount.clearParameters();
        querySelectAccount.setString(1, account.getUsername());
        ResultSet resultSet = querySelectAccount.executeQuery();
        if (resultSet.next())
            return new Account(resultSet.getInt(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5));
        else
            throw new SQLException("Account not found");
    }


    public boolean updateAccountCredentials(Account account) {
        try {
            queryUpdateAccountCredentials.clearParameters();
            queryUpdateAccountCredentials.setString(1, account.getPassword());
            queryUpdateAccountCredentials.setString(2, account.getSalt());
            queryUpdateAccountCredentials.setInt(3, account.getIterations());
            queryUpdateAccountCredentials.setInt(4, account.getAid());
            if (!(queryUpdateAccountCredentials.executeUpdate() == 0))
                return true;
        } catch (SQLException e) {
        }
        return false;
    }

    public Contact getContact(Account account) throws SQLException {
        querySelectContact.clearParameters();
        querySelectContact.setString(1, account.getUsername());
        ResultSet resultSet = querySelectContact.executeQuery();
        if (resultSet.next())
            return new Contact(resultSet.getInt(1));
        else
            throw new SQLException("Contact not found");
    }

    /**
     * Add a new contact to the database for the specified network along with the account details
     *
     * @param contact the contact to add
     * @param network the network to add it to
     * @return true for success and false for failure
     */
    public boolean addUser(Contact contact, Network network, Account account) {

        try {
            try {
                conn.setAutoCommit(false);
                account.setAid(accountCounter++);
                addContact(contact);
                addAccount(account);
                addAccountContact(account, contact);
                addNetworkContact(contact, network);
                addAccountNetwork(account, network);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
        }
        return false;
    }

    private void addAccountContact(Account account, Contact contact) throws SQLException {
        queryInsertAccountContact.clearParameters();
        queryInsertAccountContact.setInt(1, account.getAid());
        queryInsertAccountContact.setInt(2, contact.getCid());
        if (queryInsertAccountContact.executeUpdate() == 0)
            throw new SQLException();
    }

    private void addAccount(Account account) throws SQLException {
        queryInsertAccount.clearParameters();
        queryInsertAccount.setInt(1, account.getAid());
        queryInsertAccount.setString(2, account.getUsername());
        queryInsertAccount.setString(3, account.getPassword());
        queryInsertAccount.setString(4, account.getSalt());
        queryInsertAccount.setInt(5, account.getIterations());
        if (queryInsertAccount.executeUpdate() == 0)
            throw new SQLException();

    }

    private void addContact(Contact contact) throws SQLException {
        queryInsertContact.setInt(1, contact.getCid());
        queryInsertContact.setString(2, contact.getAlias());
        if (queryInsertContact.executeUpdate() == 0)
            throw new SQLException();
    }

    /**
     * adds the associative entity record connecting the new contact and the network it pertains to
     *
     * @param contact the contact to connect
     * @param network the network to connect it to
     * @throws SQLException
     */
    private void addNetworkContact(Contact contact, Network network) throws SQLException {
        queryInsertNetworkContacts.setInt(1, network.getNid());
        queryInsertNetworkContacts.setInt(2, contact.getCid());
        if (queryInsertNetworkContacts.executeUpdate() == 0)
            throw new SQLException();
    }

    private void addAccountNetwork(Account account, Network network) throws SQLException {
        queryInsertAccountNetwork.setInt(1, account.getAid());
        queryInsertAccountNetwork.setInt(2, network.getNid());
        if (queryInsertAccountNetwork.executeUpdate() == 0)
            throw new SQLException();
    }

    /**
     * delete a contact from the database for the specified network
     * @param contact the contact to be deleted
     * @return true for success and false for failure
     */
    public boolean deleteUser(Contact contact){

        try {
            try {
                conn.setAutoCommit(false);
                deleteAccount(contact);
                deleteContact(contact);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        }catch (SQLException e){}
        return false;
    }

    private void deleteAccount(Contact contact) throws SQLException{
        queryDeleteAccount.clearParameters();
        queryDeleteAccount.setInt(1, contact.getCid());
        if(queryDeleteAccount.executeUpdate() == 0)
            throw new SQLException();
    }


    private void deleteContact(Contact contact) throws SQLException{
        queryDeleteContact.clearParameters();
        queryDeleteContact.setInt(1, contact.getCid());
        if (queryDeleteContact.executeUpdate() == 0)
            throw new SQLException();
    }

    public Set<String> getUsernames() throws SQLException{
        Set<String> usernames = ConcurrentHashMap.newKeySet();
        ResultSet resultSet = querySelectUsernames.executeQuery();
        if(resultSet.next()){
            do {
                usernames.add(resultSet.getString(1));
            } while (resultSet.next());
        }
        return usernames;
    }



//
//    /**
//     * Add a chat record to the database for the relevant account and contact
//     * @param uid the account id
//     * @param cid the contact id
//     * @return true if the operation is successful and false if not
//     */
//    public boolean addChat( int uid, String cid){
//
//        // COULD VALIDATE THE INPUTS HERE
//        try{
//            queryInsertChat.setInt(1, uid);
//            queryInsertChat.setString(2, cid);
//            if(queryInsertChat.executeUpdate() > 0)
//            return true;
//
//        } catch(SQLException e){
//            System.out.println("Failed to add chat: " + e.getMessage());
//        }
//    return false;
//}
//
//        /**
//         * Adds a link between the chat table and new messages added to the database
//         * @param uid account identifier is part of the chat composite pk
//         * @param cid contact identifier is part of the chat composite pk
//         * @param mid message identifer is the primary key of the messages table
//         * @return true is returned on success
//         */
//    private boolean addChatMessages(int uid, String cid, int mid){
//
//        try{
//            queryInsertChatMessages.setInt(1, uid);
//            queryInsertChatMessages.setString(2, cid);
//            queryInsertChatMessages.setInt(3, mid);
//            if(queryInsertChatMessages.executeUpdate() > 0)
//            return true;
//
//        }catch(SQLException e){
//            System.out.println("Failed to add chat message record: " + e.getMessage());
//        }
//
//        return false;
//
//    }
//
//
//    /**
//     * Add a message record to the database
//     * @param cid the contact id
//     * @param uid the account id
//     * @param message the message string encrypted with the accounts public key
//     * @param dt the datetime string
//     * @param status the status enum to determine if the message was sent, recieved or pending successful transmission
//     * @return true if the operation is successful and false if not
//     */
//     boolean addMessage(String cid,int uid, String message, long dt, MessageStatus status){
//
//         // !!! WOULD BE BETTER WITH BATCH IF POSSIBLE
//         // add a message size limit of 255 chars
//
//         try {
//            int mid = messageCounter++;
//
//                queryInsertMessage.setInt(1,mid);
//                queryInsertMessage.setString(2,message);
//                queryInsertMessage.setLong(3, dt);
//                queryInsertMessage.setInt(4,status.getCode());
//                if(queryInsertMessage.executeUpdate() > 0) {
//                    if (addChatMessages(uid, cid, mid))
//                        return true;
//                }
//
//        }catch(SQLException e){
//            System.out.println("Failed to add message: " + e.getMessage());
//    }
//    return false;
//}
//
//
//    boolean addRetained(int uid, String cid, RetainedDatatype datatype){
//
//         try{
//             queryInsertRetain.setInt(1, uid);
//             queryInsertRetain.setString(2, cid);
//             queryInsertRetain.setInt(3, datatype.getCode());
//             if(queryInsertRetain.executeUpdate() >0)
//             return true;
//
//         }catch(SQLException e){
//             System.out.println("Failed to add retained record: " + e.getMessage());
//         }
//
//         return false;
//    }



// A TEMPORARY METHOD FOR TESTING PURPOSES
public boolean tempMethod(){

        try {
            Statement statement = conn.createStatement();
            statement.execute("DELETE FROM contacts");
            statement.execute("DELETE FROM accounts");
            statement.execute("DELETE FROM accountContact");
            statement.execute("DELETE FROM chats");
            statement.execute("DELETE FROM chatMessages");
            statement.execute("DELETE FROM messages");
            statement.execute("DELETE FROM retained");

            return true;
        }catch(SQLException e){
            System.out.println(e.getMessage());
}
return false;
}

}
