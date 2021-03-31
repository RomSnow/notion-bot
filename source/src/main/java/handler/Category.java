package handler;

import java.util.ArrayList;

public class Category extends FileCreator {
    public Category(String name, String prePath) {
        super(name, prePath, FileType.Directory);
    }

    public void addMessage(String msg) {
    }

    public String getMessage(int messageId) {
        return "";
    }

    public ArrayList<String> getAllMessages() {
        return new ArrayList<>();
    }
}
