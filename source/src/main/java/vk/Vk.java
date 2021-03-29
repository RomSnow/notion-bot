package vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Keyboard;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;

import java.util.List;
import java.util.Random;


public class Vk {
    public static void startBot() throws ClientException, ApiException, InterruptedException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        Random random = new Random();
        GroupActor actor = new GroupActor(
                203495247,
                "5ca5afd12f9cf480d275deb1ded1e4cf3aa3d5daee2a8bad5f776da79f8cc6670bcecdb4c9e068321012c"
        );
        Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();
        while (true){
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(actor).ts(ts);
            List<Message> messagesHistory = historyQuery.execute().getMessages().getItems();
            if (!messagesHistory.isEmpty())
                for (var message: messagesHistory) {
                    var messageText = message.getText();
                    var userId = message.getFromId();
                    System.out.println("Vk message from User: " + userId + ", data: " + messageText);
                    try {
                        vk.messages().send(actor).message(messageText).userId(userId).
                                randomId(random.nextInt(10000)).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
            Thread.sleep(500);
        }
    }

    public static void main(String[] args) throws InterruptedException, ClientException, ApiException {
        startBot();
    }
}
