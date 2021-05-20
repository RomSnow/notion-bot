package vk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.messages.*;
import handler.Handler;

import java.util.Random;

public class VkMessageHandler {
    protected final Handler handler = new Handler("src/main/resources");
    protected final Random random = new Random();
    protected final GroupActor actor = new GroupActor(
            203495247,
            "5ca5afd12f9cf480d275deb1ded1e4cf3aa3d5daee2a8bad5f776da79f8cc6670bcecdb4c9e068321012c"
    );

    public synchronized void handleMessage(Message message, VkApiClient vk, RoomHandler roomHandler, CategoryHandler categoryHandler,
                                           FileHandler fileHandler, MessageHandler messageHandler){
        try {
            if (messageHandler.currentRoom == null && messageHandler.currentCategory == null)
                roomHandler.chooseRoom(message, vk, categoryHandler, messageHandler);
            else{
                if (messageHandler.currentCategory == null)
                    categoryHandler.chooseCategory(message, vk, roomHandler, fileHandler, messageHandler);
                else
                    fileHandler.chooseFile(message, vk, categoryHandler, messageHandler);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
