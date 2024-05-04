import java.io.Serializable;
import java.util.Date;

public class Request implements Serializable, Comparable<Request> {

    private static final long serialVersionUID = 1L;
    public static final int DOWNLOAD = 1;
    public static final int UPLOAD = 2;
    public static final int SEARCH = 3;
    public static final int DELETE = 4;


    //private int logicalClock;
    String sender,requestId; // Assuming requestId is part of the request
    int pid,operation,clock;
    //private Operation operation;
    private String fileName;
    private byte[] data;
    private Date lastModifiedDate;
    private boolean result;
    public static final int REQUEST_VOTE = 5; // Add a new operation for RequestVote
    // Attributes for RequestVote
    private int candidateId;
    private int term;
    public Request(){

    }
    // Constructor for RequestVote
    public Request(int clock, int candidateId, int term) {
        this.clock = clock;
        this.operation = REQUEST_VOTE;
        this.candidateId = candidateId;
        this.term = term;
    }
    public Request(int clock, String sender, String requestId,int pid, int operation, String fileName, byte[] data, Date lastModifiedDate, boolean result) {
        this.clock = clock;
        this.sender = sender;
        this.requestId = requestId;
        this.pid = pid;
        this.operation = operation;
        this.fileName = fileName;
        this.data = data;
        this.lastModifiedDate = lastModifiedDate;
        this.result = result;
    }


    public boolean isResult() {
        return result;
    }

    public Request(int clock) {
        this.clock = clock;
    }

    public int getclock() {
        return clock;
    }

    public void setLogicalClock(int logicalClock) {
        this.clock = logicalClock;
    }

    public void setName(String fileName){
        this.fileName = fileName;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
    public String getSender() {return sender;}
    public String getRequestId() {return requestId;}

    public void setRequestId(String requestId) {this.requestId = requestId;}

    public void setSender(String sender) {this.sender = sender;}
    @Override
    public int compareTo(Request other) {
        if(this.clock == other.clock)
            return this.pid - other.pid;
        return this.clock - other.clock;
    }
}
