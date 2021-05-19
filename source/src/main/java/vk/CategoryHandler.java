package vk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.*;
import handler.Room;

import java.util.ArrayList;
import java.util.List;

public class CategoryHandler extends VkMessageHandler {

    protected synchronized void chooseCategory(Message message, VkApiClient vk, RoomHandler roomHandler,
                                               FileHandler fileHandler, MessageHandler messageHandler) throws ClientException, ApiException {
        var sendMessage = false;
        var text = message.getText();
        if (text.equals("back") || text.equals("delete room")){
            var answer = "back";
            if (text.equals("delete room")) {
                //handler.removeRoom()
                answer = "room deleted";
            }
            messageHandler.currentRoom = null;
            vk.messages().send(actor).message(answer).userId(message.getFromId()).keyboard(roomHandler.setRoomKeyboard()).
                    randomId(random.nextInt(10000)).execute();
        }
        else{
            if (text.equals("new category")){
                messageHandler.newCategory = true;
                vk.messages().send(actor).message("fill name catedory").userId(message.getFromId()).
                        randomId(random.nextInt(10000)).execute();
            }
            else{
                if (messageHandler.newCategory){
                    messageHandler.newCategory = false;
                    messageHandler.currentRoom.addCategory(text);
                    vk.messages().send(actor).message("category created").userId(message.getFromId()).keyboard(setCategoryKeyboard(messageHandler.currentRoom)).
                            randomId(random.nextInt(10000)).execute();
                }
                else {
                    for (var category : messageHandler.currentRoom.getAllCategories()) {
                        if (category.getName().equals(text)) {
                            messageHandler.currentCategory = category;
                            vk.messages().send(actor).message("current category:" + messageHandler.currentCategory.getName()).
                                    userId(message.getFromId()).keyboard(fileHandler.setFileKeyboard(messageHandler.currentCategory)).
                                    randomId(random.nextInt(10000)).execute();
                            sendMessage = true;
                            break;
                        }
                    }
                    if (!sendMessage)
                        vk.messages().send(actor).message("unknown command").userId(message.getFromId()).
                                keyboard(setCategoryKeyboard(messageHandler.currentRoom)).
                                randomId(random.nextInt(10000)).execute();
                }
            }
        }
}

    public Keyboard setCategoryKeyboard(Room currentRoom){
        var keyboard = new Keyboard();
        List<List<KeyboardButton>> buttons = new ArrayList<>();
        List<KeyboardButton> row = new ArrayList<>();
        var countCategory = 0;
        for (var category: currentRoom.getAllCategories()){
            countCategory++;
            row.add(new KeyboardButton().setAction( new KeyboardButtonAction().setLabel(category.getName()).setType(KeyboardButtonActionType.TEXT)));
            if (countCategory % 3 == 0){
                buttons.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty())
            buttons.add(row);
        List<KeyboardButton> secondRow = new ArrayList<>();
        secondRow.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("new category").setType(KeyboardButtonActionType.TEXT)));
        secondRow.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("delete room").setType(KeyboardButtonActionType.TEXT)));
        secondRow.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("back").setType(KeyboardButtonActionType.TEXT)));
        buttons.add(secondRow);
        keyboard.setButtons(buttons);
        return keyboard;
    }
}
