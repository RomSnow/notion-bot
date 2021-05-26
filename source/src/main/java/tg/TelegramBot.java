package tg;

import com.vk.api.sdk.objects.messages.Keyboard;
import com.vk.api.sdk.objects.messages.KeyboardButtonAction;
import com.vk.api.sdk.objects.messages.KeyboardButtonActionType;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.A;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.facilities.filedownloader.TelegramFileDownloader;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import state.Answer;
import state.Response;
import state.State;
import vk.Vk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static config.Config.getConfig;

public class TelegramBot extends TelegramLongPollingBot {
    private final String username;
    private final String token;
    private final HashMap<Long, State> chatIdToState;

    public TelegramBot() {
        token = getConfig().getString("tg.token");
        username = getConfig().getString("tg.username");
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
        else {
            sendDocByChatId(chatId.toString(), response);
            response.getFile().logOut();
        }

    }

    private void sendMessageByChatId(String chatId, String message) {
        var sendMessage = new SendMessage(chatId, message);
        sendMessage.setReplyMarkup(setKeyboard(message));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDocByChatId(String chatId, Response response) {
        try {
            var fileToSend = new java.io.File("source/src/main/resources/tmp/" + response.getFile().getName());
            FileUtils.copyFile(response.getFile().getFile(), fileToSend);
            var inputFile = new InputFile(fileToSend);
            var sendDoc = new SendDocument(chatId, inputFile);
            execute(sendDoc);
        } catch (IOException | TelegramApiException e) {
            sendMessageByChatId(chatId, Answer.ErrWhileSendingFile);
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

    private static ReplyKeyboardMarkup setKeyboard(String text){
        System.out.println(text);
        KeyboardRow row = new KeyboardRow();
        ArrayList<KeyboardRow> buttons = new ArrayList<>();
        var keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setSelective(true);
        for (var wordToSend: Vk.setKeyboard(text)){
            row.add(new KeyboardButton(wordToSend));
            if (row.size() == 3){
                buttons.add(row);
                row = new KeyboardRow();
            }
        }
        buttons.add(row);
        keyboard.setKeyboard(buttons);
        return keyboard;
    }
}
