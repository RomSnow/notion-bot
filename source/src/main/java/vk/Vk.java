package vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.apache.commons.io.FileUtils;
import tg.state.State;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Vk {
    private static final TransportClient transportClient = new HttpTransportClient();
    private static final VkApiClient vk = new VkApiClient(transportClient);
    private static final GroupActor actor = new GroupActor(203495247,
            "5ca5afd12f9cf480d275deb1ded1e4cf3aa3d5daee2a8bad5f776da79f8cc6670bcecdb4c9e068321012c");

    public static synchronized void startBot() throws ClientException, ApiException, InterruptedException {
        Map<Integer, State> users = new HashMap<>();
        Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();
        while (true){
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(actor).ts(ts);
            List<Message> messagesHistory = historyQuery.execute().getMessages().getItems();
            if (!messagesHistory.isEmpty())
                for (var message: messagesHistory) {
                    var random = new Random();
                    if (!users.containsKey(message.getFromId())) {
                        var state = new State();
                        users.put(message.getFromId(), state);
                        var word = message.getText();
                        if (!message.getAttachments().isEmpty()) {
                            var fileName = message.getAttachments().get(0).getDoc().getTitle();
                            var fileUrl = message.getAttachments().get(0).getDoc().getUrl();
                            var file = new File(fileUrl.toString());
                            try {
                                FileUtils.copyURLToFile(fileUrl, file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            var response = state.transition(word, file, fileName);
                            vk.messages().send(actor).message(response.getText()).userId(message.getFromId()).
                                    randomId(random.nextInt(10000)).execute();
                        }
                        var response = state.transition(word, null, "");
                        vk.messages().send(actor).message(response.getText()).userId(message.getFromId()).
                                randomId(random.nextInt(10000)).execute();
                    }
                    else {
                        if (!message.getAttachments().isEmpty()) {
                            var fileName = message.getAttachments().get(0).getDoc().getTitle();
                            var fileUrl = message.getAttachments().get(0).getDoc().getUrl();
                            var file = new File(fileUrl.toString());
                            try {
                                FileUtils.copyURLToFile(fileUrl, file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            var state = users.get(message.getFromId());
                            users.put(message.getFromId(), state);
                            var word = message.getText();
                            var response = state.transition(word, file, fileName);
                            vk.messages().send(actor).message(response.getText()).userId(message.getFromId()).
                                    randomId(random.nextInt(10000)).execute();
                        }
                        var state = users.get(message.getFromId());
                        var word = message.getText();
                        var response = state.transition(word, null, "");
                        if (response.getFile() == null) {
                            vk.messages().send(actor).message(response.getText()).userId(message.getFromId()).
                                    randomId(random.nextInt(10000)).execute();
                        } else {
                            var userId = message.getFromId();
                            var peerId = message.getPeerId();
                            var fileToSend = new File("source/src/main/resources/tmp/" + response.getFile().getName());
                            try {
                                FileUtils.copyFile(response.getFile().getFile(), fileToSend);
                            } catch (IOException e) {
                                vk.messages().send(actor).message("problem").userId(message.getFromId()).
                                        randomId(random.nextInt(10000)).execute();
                            }
                            var server = vk.docs().getMessagesUploadServer(actor).peerId(peerId).execute();
                            var upload = vk.upload().doc(server.getUploadUrl().toString(),
                                    fileToSend).execute();
                            var docs = vk.docs().save(actor, upload.getFile()).execute();
                            var doc = docs.getDoc();
                            vk.messages().send(actor).message("").attachment("doc" + doc.getOwnerId() + "_" + doc.getId()).
                                    userId(userId).
                                    randomId(random.nextInt(10000)).execute();
                        }
                    }
                }
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
            Thread.sleep(500);
        }
    }
}
