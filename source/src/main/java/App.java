import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import tg.TelegramBot;
import vk.Vk;

public class App {
    public static void main(String[] args) {
        var tgThread = new Thread(() -> {
            try {
                var botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new TelegramBot());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        var vkThread = new Thread(Vk::startBot);
        tgThread.start();
        vkThread.start();
        System.out.println("Bots started...");
    }
}
