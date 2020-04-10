package datasource;

public class Message {

    private int mid;
    private String message;
    private long dt;
    private MessageStatus status;

    /**
     * for new messages which don't yet have an mid
     * @param message the encrypted message
     * @param dt the datetime of the message
     * @param status the transmission status
     */
    public Message(String message, long dt, MessageStatus status){
        this(-1, message, dt, status);
    }

    /**
     * for messages with an established mid
     * @param mid the message identifer
     * @param message the encrypted message
     * @param dt the datetime of the message
     * @param status the transmission status
     */
    public Message(int mid, String message, long dt, MessageStatus status) {
        this.mid = mid;
        this.message = message;
        this.dt = dt;
        this.status = status;
    }

    public int getMid() {
        return mid;
    }

    public String getMessage() {
        return message;
    }

    public long getDt() {
        return dt;
    }

    public MessageStatus getStatus() {
        return status;
    }
}
