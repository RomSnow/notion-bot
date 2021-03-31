package handler;

public class InvalidIdException extends Exception{
    private final int id;

    public InvalidIdException(int id){
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Invalid ID: %s".formatted(id);
    }
}
