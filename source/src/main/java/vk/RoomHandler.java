package vk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.*;

import java.util.ArrayList;
import java.util.List;

public class RoomHandler extends VkMessageHandler{
    protected synchronized void chooseRoom(Message message, VkApiClient vk, CategoryHandler categoryHandler,
                                           MessageHandler messageHandler) throws ClientException, ApiException {
        var text = message.getText();
        var sendMessage = false;
        if (text.equals("new room")){
            messageHandler.newRoom = true;
            vk.messages().send(actor).message("fill name room").userId(message.getFromId()).
                    randomId(random.nextInt(10000)).execute();
        }
        else {
            if (messageHandler.newRoom) {
                messageHandler.newRoom = false;
                handler.registerRoom(text);
                vk.messages().send(actor).message("room created").userId(message.getFromId()).keyboard(setRoomKeyboard()).
                        randomId(random.nextInt(10000)).execute();
            } else {
                for (var room : handler.getAllRooms()) {
                    if (room.getName().equals(text)) {
                        messageHandler.currentRoom = room;
                        vk.messages().send(actor).message("current room:" + messageHandler.currentRoom.getName()).
                                userId(message.getFromId()).keyboard(categoryHandler.setCategoryKeyboard(messageHandler.currentRoom)).
                                randomId(random.nextInt(10000)).execute();
                        sendMessage = true;
                        break;
                    }
                }
                if (!sendMessage)
                    vk.messages().send(actor).message("unknown command").userId(message.getFromId()).keyboard(setRoomKeyboard()).
                            randomId(random.nextInt(10000)).execute();
            }
        }
    }

    public Keyboard setRoomKeyboard(){
        var keyboard = new Keyboard();
        List<List<KeyboardButton>> buttons = new ArrayList<>();
        List<KeyboardButton> row = new ArrayList<>();
        var countRoom = 0;
        for (var room: handler.getAllRooms()){
            countRoom++;
            row.add(new KeyboardButton().setAction( new KeyboardButtonAction().setLabel(room.getName()).setType(KeyboardButtonActionType.TEXT)));
            if (countRoom % 3 == 0){
                buttons.add(row);
                row = new ArrayList<>();
            }
        }
        row.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("new room").setType(KeyboardButtonActionType.TEXT)));
        buttons.add(row);
        keyboard.setButtons(buttons);
        return keyboard;
    }
}
