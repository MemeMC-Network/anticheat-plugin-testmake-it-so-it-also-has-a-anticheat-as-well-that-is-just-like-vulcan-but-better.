package network.mememc.memeac.data;

import network.mememc.memeac.checks.Check;

public class ViolationData {
    
    private final Check check;
    private final String reason;
    private final int certainty;
    private final long timestamp;
    
    public ViolationData(Check check, String reason, int certainty, long timestamp) {
        this.check = check;
        this.reason = reason;
        this.certainty = certainty;
        this.timestamp = timestamp;
    }
    
    public Check getCheck() {
        return check;
    }
    
    public String getReason() {
        return reason;
    }
    
    public int getCertainty() {
        return certainty;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getFormattedMessage() {
        return String.format("[%s] %s (Certainty: %d%%)", check.getName(), reason, certainty);
    }
}
