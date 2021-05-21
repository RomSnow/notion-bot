package vk;

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
import java.util.*;


public class Vk {
    private static final TransportClient transportClient = new HttpTransportClient();
    private static final VkApiClient vk = new VkApiClient(transportClient);
    private static final GroupActor actor = new GroupActor(203495247,
            "5ca5afd12f9cf480d275deb1ded1e4cf3aa3d5daee2a8bad5f776da79f8cc6670bcecdb4c9e068321012c");
    private static final HashMap<Integer, State> users = new HashMap<>();

    public static synchronized void startBot() {
        while (true){
            try {
                var ts = vk.messages().getLongPollServer(actor).execute().getTs();
                Thread.sleep(300);
                var historyQuery = vk.messages().getLongPollHistory(actor).ts(ts);
                var messagesHistory = historyQuery.execute().getMessages().getItems();

                if (!messagesHistory.isEmpty())
                    for (var message : messagesHistory) {
                        if (!users.containsKey(message.getFromId())) {
                            var state = new State();
                            users.put(message.getFromId(), state);

                            var response = state.transition(message.getText(), null, "");
                            sendMessage(response.getText(), message.getFromId());
                        } else {
                            var state = users.get(message.getFromId());

                            if (!message.getAttachments().isEmpty()) {
                                var response = handleGetFile(message, state);
                                sendMessage(response.getText(), message.getFromId());
                            }

                            var response = state.transition(message.getText(), null, "");
                            if (response.getFile() == null) {
                                sendMessage(response.getText(), message.getFromId());
                            } else {
                                handleSendFile(message, response);
                            }
                        }
                    }
            } catch (InterruptedException | ApiException | ClientException e) {
                e.printStackTrace();
            }
        }
    }

    private static Response handleGetFile(Message msg, State state) {
        var word = msg.getText();
        var fileName = msg.getAttachments().get(0).getDoc().getTitle();
        var fileUrl = msg.getAttachments().get(0).getDoc().getUrl();
        var file = new File(fileUrl.toString());
        try {
            FileUtils.copyURLToFile(fileUrl, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return state.transition(word, file, fileName);
    }

    private static void handleSendFile(Message msg, Response resp) {
        try {
            var fileToSend = new File("source/src/main/resources/tmp/" + resp.getFile().getName());
            FileUtils.copyFile(resp.getFile().getFile(), fileToSend);
            var server = vk.docs().getMessagesUploadServer(actor).peerId(msg.getPeerId()).execute();
            var uploader = vk.upload().doc(server.getUploadUrl().toString(), fileToSend).execute();
            var doc = vk.docs().save(actor, uploader.getFile()).execute().getDoc();
            sendAttachment(doc.getOwnerId(), doc.getId(), msg.getFromId());
        } catch (IOException | ClientException | ApiException e) {
            sendMessage(Answer.ErrWhileSendingFile, msg.getFromId());
            e.printStackTrace();
        }
    }

    private static void sendMessage(String text, Integer userId) {
        try {
            var random = new Random();
            vk.messages().send(actor)
                    .message(text)
                    .userId(userId)
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
}
