package vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;

import java.util.*;


public class Vk {
    private static final TransportClient transportClient = new HttpTransportClient();
    private static final VkApiClient vk = new VkApiClient(transportClient);
    private static final GroupActor actor = new GroupActor(      203495247,
            "5ca5afd12f9cf480d275deb1ded1e4cf3aa3d5daee2a8bad5f776da79f8cc6670bcecdb4c9e068321012c"
    );

    public static synchronized void startBot() throws ClientException, ApiException, InterruptedException {
        Map<Integer, MessageHandler> users = new HashMap<>();
        Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();
        var roomHandler = new RoomHandler();
        var categoryHandler = new CategoryHandler();
        var vkMessageHandler = new VkMessageHandler();
        var fileHandler = new FileHandler();
        while (true){
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(actor).ts(ts);
            List<Message> messagesHistory = historyQuery.execute().getMessages().getItems();
            if (!messagesHistory.isEmpty())
                for (var message: messagesHistory) {
                    if (!users.containsKey(message.getFromId())) {
                        var messageHandler = new MessageHandler();
                        users.put(message.getFromId(), messageHandler);
                        vkMessageHandler.handleMessage(message, vk, roomHandler, categoryHandler, fileHandler, messageHandler);
                    }
                    else {
                        var messageHandler = users.get(message.getFromId());
                        vkMessageHandler.handleMessage(message, vk, roomHandler, categoryHandler, fileHandler, messageHandler);
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
