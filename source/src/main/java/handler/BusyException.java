package handler;

public class BusyException extends Exception {
    private final String msg;
    public BusyException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
