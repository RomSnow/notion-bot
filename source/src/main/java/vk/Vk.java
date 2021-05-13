package vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import handler.Handler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Vk {
    private static boolean newRoom = false;

    public static void startBot() throws ClientException, ApiException, InterruptedException {
        var handler = new Handler();
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        GroupActor actor = new GroupActor(
                203495247,
                "5ca5afd12f9cf480d275deb1ded1e4cf3aa3d5daee2a8bad5f776da79f8cc6670bcecdb4c9e068321012c"
        );
        UserActor userActor = new UserActor(
                154175388,
                "773e5a0b3d68b66248bc3909afc1a57468aaa3a5fb2d436d3b0fb18002af5b2878d76bbcc6d9c577eb586");
        Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();
        while (true){
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(actor).ts(ts);
            List<Message> messagesHistory = historyQuery.execute().getMessages().getItems();
            if (!messagesHistory.isEmpty())
                for (var message: messagesHistory) {
                    var messageText = message.getText();
                    var userId = message.getFromId();
                    var files = message.getAttachments();
                    System.out.println("Vk message from User: " + userId + ", data: " + messageText);
                    try {
                        for (var file: files) {
                            var url = file.getDoc().getUrl();
                            var name = file.getDoc().getTitle();
                            FileUtils.copyURLToFile(url, FileUtils.getFile(new File("scr/main/resources/" + name), name));
                        }
                        sendMessage(messageText, vk, actor, userId, handler).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
            Thread.sleep(500);
        }
    }

    public static MessagesSendQuery sendMessage(String answer, VkApiClient vk, GroupActor actor, Integer userId,
                                                Handler handler){
        Random random = new Random();
        if (!newRoom) {
            if (answer.equals("new room")) {
                newRoom = true;
                return vk.messages().send(actor).message("введите название комнаты").userId(userId).
                        randomId(random.nextInt(10000));
            }
        }
        else {
            handler.registerRoom(answer);
            return vk.messages().send(actor).message("").userId(userId).keyboard(setKeyboard(handler)).
                    randomId(random.nextInt(10000));
        }
        return vk.messages().send(actor).message("help").keyboard(setKeyboard(handler)).userId(userId).
                randomId(random.nextInt(10000));
    }

    public static Keyboard setKeyboard(Handler handler){
        var currentRooms = handler.getAllRooms();
        Keyboard keyboard = new Keyboard();
        List<List<KeyboardButton>> buttons = new ArrayList<>();
        List<KeyboardButton> row = new ArrayList<>();
        for (var room: currentRooms)
            row.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel(room.getName()).
                    setType(KeyboardButtonActionType.TEXT)));
        row.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("new room").
                setType(KeyboardButtonActionType.TEXT)));
        buttons.add(row);
        keyboard.setButtons(buttons);
        return keyboard;
    }

    public static void main(String[] args) throws InterruptedException, ClientException, ApiException {
        startBot();
    }
}
