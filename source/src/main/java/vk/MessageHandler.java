package vk;

import handler.Category;
import handler.Handler;
import handler.Room;

public class MessageHandler {
    protected boolean newRoom;
    protected boolean newCategory;
    protected boolean newFile;
    protected final Handler handler = new Handler("source/src/main/resources/Rooms");
    protected Room currentRoom;
    protected Category currentCategory;

    public MessageHandler(){
        newRoom = false;
        newCategory = false;
        newFile = false;
        currentRoom = null;
        currentCategory = null;
    }
}
