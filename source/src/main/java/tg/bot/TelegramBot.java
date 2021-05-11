package tg.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static config.Config.getConfig;

public class TelegramBot extends TelegramLongPollingBot {
    private final String username;
    private final String token;

    public TelegramBot() {
        token = getConfig().get("tg.token");
        username = getConfig().get("tg.username");
    }

    @Override
    public void onUpdateReceived(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        var text = message.getText();

        sendMessageByChatId(chatId.toString(), text);
    }

    private void sendMessageByChatId(String chatId, String message) {
        var sendMessage = new SendMessage(chatId, message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
