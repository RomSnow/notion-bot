package vk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.*;
import handler.Category;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler extends VkMessageHandler{
    protected synchronized void chooseFile(Message message, VkApiClient vk, CategoryHandler categoryHandler,
                                           MessageHandler messageHandler) throws ClientException, ApiException, IOException {
        var sendMessage = false;
        var text = message.getText();
        if (text.equals("back") || text.equals("delete category")){
            //if (text.equals("delete category"))
                //handler.removeCategory()
            messageHandler.currentCategory = null;
            vk.messages().send(actor).message("category deleted").userId(message.getFromId()).
                    keyboard(categoryHandler.setCategoryKeyboard(messageHandler.currentRoom)).
                    randomId(random.nextInt(10000)).execute();
        }
        else{
            if (text.equals("new file")){
                messageHandler.newFile = true;
                vk.messages().send(actor).message("send file").userId(message.getFromId()).
                        randomId(random.nextInt(10000)).execute();
            }
            else{
                if (messageHandler.newFile){
                    messageHandler.newFile = false;
                    if (message.getAttachments().isEmpty())
                        vk.messages().send(actor).message("send file or select back").userId(message.getFromId()).
                                randomId(random.nextInt(10000)).execute();
                    else{
                        for (var file: message.getAttachments()){
                            var name = file.getDoc().getTitle();
                            File fileToSave = new File("src/main/resources/" + name);
                            var url = file.getDoc().getUrl();
                            FileUtils.copyURLToFile(url, fileToSave);
                            messageHandler.currentCategory.addFile(fileToSave);
                            vk.messages().send(actor).message("file append").userId(message.getFromId()).
                                    keyboard(setFileKeyboard(messageHandler.currentCategory)).
                                    randomId(random.nextInt(10000)).execute();
                        }
                    }
                }
                else {
                    for (var file : messageHandler.currentCategory.getAllFiles()) {
                        if (file.getName().equals(text)) {
                            FileUtils.copyFile(file.getFile(), new File("src/main/resources/" + file.getName()));
                            var userId = message.getFromId();
                            var peerId = message.getPeerId();
                            var server = vk.docs().getMessagesUploadServer(actor).peerId(peerId).execute();
                            var response = vk.upload().doc(server.getUploadUrl().toString(),
                                    new File("src/main/resources/" + file.getName())).execute();
                            var docs = vk.docs().save(actor, response.getFile()).execute();
                            var doc = docs.getDoc();
                            vk.messages().send(actor).message("asked file").attachment("doc" + doc.getOwnerId() + "_" + doc.getId()).
                                    userId(userId).
                                    randomId(random.nextInt(10000)).execute();
                            sendMessage = true;
                            break;
                        }
                    }
                    if (!sendMessage)
                        vk.messages().send(actor).message("unknown command").userId(message.getFromId()).
                                keyboard(setFileKeyboard(messageHandler.currentCategory)).
                                randomId(random.nextInt(10000)).execute();
                }
            }
        }
    }

    public Keyboard setFileKeyboard(Category currentCategory){
        var keyboard = new Keyboard();
        List<List<KeyboardButton>> buttons = new ArrayList<>();
        List<KeyboardButton> row = new ArrayList<>();
        var countFiles = 0;
        for (var file: currentCategory.getAllFiles()){
            countFiles++;
            row.add(new KeyboardButton().setAction( new KeyboardButtonAction().setLabel(file.getName()).setType(KeyboardButtonActionType.TEXT)));
            if (countFiles % 3 == 0){
                buttons.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            buttons.add(row);
            row = new ArrayList<>();
        }
        row.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("new file").setType(KeyboardButtonActionType.TEXT)));
        row.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("delete category").setType(KeyboardButtonActionType.TEXT)));
        row.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("back").setType(KeyboardButtonActionType.TEXT)));
        buttons.add(row);
        keyboard.setButtons(buttons);
        return keyboard;
    }
}
