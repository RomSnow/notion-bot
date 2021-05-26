package access_manager;

public class InvalidPasswordException extends Exception {
    private final String msg;

    public InvalidPasswordException (String password) {
        msg = "Invalid password %s".formatted(password);
    }

    public String getMsg() {
        return msg;
    }
}
