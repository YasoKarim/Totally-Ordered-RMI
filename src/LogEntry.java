import java.io.Serializable;

public class LogEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private int term;
    private int leaderId;
    private Request request;

    public LogEntry(int term, int leaderId, Request request) {
        this.term = term;
        this.leaderId = leaderId;
        this.request = request;
    }

    // Getters and setters (if needed)

    @Override
    public String toString() {
        return "LogEntry{" +
                "term=" + term +
                ", leaderId=" + leaderId +
                ", request=" + request +
                '}';
    }

    public Request getRequest() {
        return null;
    }
}
