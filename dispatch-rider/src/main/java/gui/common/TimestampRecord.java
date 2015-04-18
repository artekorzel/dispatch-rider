package gui.common;

public class TimestampRecord {
    int timestamp;
    private Object data;

    TimestampRecord(int ts, Object data) {
        timestamp = ts;
        this.data = data;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

