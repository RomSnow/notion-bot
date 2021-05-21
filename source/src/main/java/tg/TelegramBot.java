package tg;

import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.facilities.filedownloader.TelegramFileDownloader;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import state.Response;
import state.State;

import java.io.IOException;
import java.util.HashMap;

import static config.Config.getConfig;

public class TelegramBot extends TelegramLongPollingBot {
    private final String username;
    private final String token;
    private final HashMap<Long, State> chatIdToState;

    public TelegramBot() {
        token = getConfig().get("tg.token");
        username = getConfig().get("tg.username");
        chatIdToState = new HashMap<>();
    }

    @Override
    public void onUpdateReceived(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        if (!chatIdToState.containsKey(chatId)) {
            chatIdToState.put(chatId, new State());
        }
        handleMessage(message, chatIdToState.get(chatId));
    }

    private void handleMessage(Message msg, State state) {
        Response response = null;
        var chatId = msg.getChatId();
        var text = msg.getText();
        var fd = new TelegramFileDownloader(new BotToken(getBotToken()));
        var doc = msg.getDocument();
        if (doc != null) {
            var gf = new GetFile(doc.getFileId());
            try {
                var filePath = execute(gf).getFilePath();
                var tgFile = new File(doc.getFileId(), doc.getFileUniqueId(), doc.getFileSize(), filePath);
                var file = fd.downloadFile(tgFile);
                response = state.transition(text, file, doc.getFileName());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            response = state.transition(text, null, "");
        }

        if (response == null)
            return;
        if (response.getFile() == null)
            sendMessageByChatId(chatId.toString(), response.getText());
        else
            sendDocByChatId(chatId.toString(), response);
    }

    private void sendMessageByChatId(String chatId, String message) {
        var sendMessage = new SendMessage(chatId, message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDocByChatId(String chatId, Response response) {
        var fileToSend = new java.io.File("source/src/main/resources/tmp/" + response.getFile().getName());
        try {
            FileUtils.copyFile(response.getFile().getFile(), fileToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var inputFile = new InputFile(fileToSend);
        var sendDoc = new SendDocument(chatId, inputFile);

        try {
            execute(sendDoc);
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
