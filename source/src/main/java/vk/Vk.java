package vk;

import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import org.apache.commons.io.FileUtils;
import state.Answer;
import state.State;
import state.Response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static config.Config.getConfig;


public class Vk {
    private static final TransportClient transportClient = new HttpTransportClient();
    private static final VkApiClient vk = new VkApiClient(transportClient);
    private static final GroupActor actor = new GroupActor(getConfig().getInteger("vk.id"),
            getConfig().getString("vk.token"));
    static Group group = new Group(getConfig().getInteger("vk.id"),
            getConfig().getString("vk.token"));
    private static final HashMap<Integer, State> users = new HashMap<>();

    public static synchronized void startBot() {
        group.onMessage(message -> {
            if (!users.containsKey(message.authorId())) {
                var state = new State();
                users.put(message.authorId(), state);
                var response = state.transition(message.getText(), null, "");
                sendMessage(response.getText(), message.authorId());
            } else {
                var state = users.get(message.authorId());
                if (message.isDocMessage()) {
                    var response = handleGetFile(message, state);
                    sendMessage(response.getText(), message.authorId());
                }

                var response = state.transition(message.getText(), null, "");
                if (response.getFile() == null) {
                    sendMessage(response.getText(), message.authorId());
                } else {
                    handleSendFile(message, response);
                    response.getFile().logOut();
                }
            }
        });
    }


    private static Response handleGetFile(Message msg, State state) {
        var word = msg.getText();
        var fileName = msg.getAttachments().getJSONObject(0).getJSONObject("doc").get("title");
        var fileUrl = msg.getAttachments().getJSONObject(0).getJSONObject("doc").get("url");
        var file = new File(fileUrl.toString());
        try {
            FileUtils.copyURLToFile(new URL(fileUrl.toString()), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return state.transition(word, file, fileName.toString());
    }

    private static void handleSendFile(Message msg, Response resp) {
        try {
            var fileToSend = new File("source/src/main/resources/tmp/" + resp.getFile().getName());
            FileUtils.copyFile(resp.getFile().getFile(), fileToSend);
            var server = vk.docs().getMessagesUploadServer(actor).peerId(msg.authorId()).execute();
            var uploader = vk.upload().doc(server.getUploadUrl().toString(), fileToSend).execute();
            var doc = vk.docs().save(actor, uploader.getFile()).execute().getDoc();
            sendAttachment(doc.getOwnerId(), doc.getId(), msg.authorId());
        } catch (IOException | ClientException | ApiException e) {
            sendMessage(Answer.ErrWhileSendingFile, msg.authorId());
            e.printStackTrace();
        }
    }

    private static void sendMessage(String text, Integer userId) {
        try {
            var random = new Random();
            vk.messages().send(actor)
                    .message(text)
                    .userId(userId)
                    .keyboard(setVkKeyboard(text))
                    .randomId(random.nextInt(10000)).execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    private static void sendAttachment(Integer docOwnerId, Integer docId, Integer userId) {
        try {
            var random = new Random();
            vk.messages().send(actor)
                    .message("")
                    .attachment("doc" + docOwnerId + "_" + docId).
                    userId(userId).
                    randomId(random.nextInt(10000)).execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    private static Keyboard setVkKeyboard(String text){
        var keyboard = new Keyboard();
        List<KeyboardButton> rowButtons = new ArrayList<>();
        List<List<KeyboardButton>> buttons = new ArrayList<>();
        var count = 0;
        for (var wordToSend: setKeyboard(text)){
            rowButtons.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel(wordToSend).setType(KeyboardButtonActionType.TEXT)));
            count++;
            if (count == 3){
                buttons.add(rowButtons);
                rowButtons = new ArrayList<>();
            }
        }
        buttons.add(rowButtons);
        if (buttons.get(0).size() > 0)
            keyboard.setButtons(buttons);
        return keyboard;
    }

    public static List<String> setKeyboard(String text){
        List<String> wordsForButtons = new ArrayList<>();
        for (var word: text.split(" ")){
            if (word.charAt(0) == '/') {
                String wordToSend = "";
                if (word.charAt(word.length() - 1) == '.' || word.charAt(word.length() - 1) == ',')
                    wordToSend = word.substring(0, word.length() - 1);
                else
                    wordToSend = word;
                wordsForButtons.add(wordToSend);
            }
        }
        return wordsForButtons;
    }
}
