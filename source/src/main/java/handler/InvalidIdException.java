package handler;

public class InvalidIdException extends Exception{
    private final String id;

    public InvalidIdException(String id){
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Invalid ID: %s".formatted(id);
    }
}
