import com.petersamokhin.vksdk.core.api.VkApi;
import com.petersamokhin.vksdk.core.client.VkApiClient;
import com.petersamokhin.vksdk.core.http.HttpClient;
import com.petersamokhin.vksdk.core.http.Parameters;
import com.petersamokhin.vksdk.core.model.VkSettings;
import com.petersamokhin.vksdk.core.model.event.MessageNew;
import com.petersamokhin.vksdk.core.model.objects.Message;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JavaBot {
    public void start() {
        int groupId = 151083290;
        String accessToken = "abcdef123456...";

        VkApiClient client = new VkApiClient(groupId, accessToken, VkApiClient.Type.Community, new VkSettings(vkHttpClient));

        client.onMessage(event -> {
            new Message()
                    .peerId(event.getMessage().getPeerId())
                    .text("Hello, world!")
                    .sendFrom(client)
                    .execute();
        });

        client.startLongPolling();

    public static void main(){
        final JavaBot bot = new JavaBot();
        bot.start();
    }
}
