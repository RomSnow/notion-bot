package tg.state;

import handler.Category;
import handler.Handler;
import handler.InvalidIdException;
import handler.Room;
import org.javatuples.Pair;
import tg.bot.Response;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class State {
    private Condition condition;
    private final HashMap<Pair<Condition, String>, Pair<Condition, String>> transition;
    private final HashMap<String, String> namesToId;
    private Handler handler;
    private Room selectedRoom;
    private Category selectedCategory;

    public State() {
        handler = new Handler("source/src/main/resources/Rooms");
        transition = new Transition().getTransitions();
        condition = Condition.BEGIN;
        namesToId = new HashMap<>();
    }

    public Response transition(String word, java.io.File file, String fileName) {
        if (Util.BeginWith(word, '/')) {
            if (this.transition.containsKey(new Pair<>(this.condition, word))) {
                if (word.equals("/goto"))
                    mockRoomAndCat();
                var out = this.transition.get(new Pair<>(condition, word));
                condition = out.getValue0();
                return new Response(out.getValue1(), null);
            }
        }
        if (condition.equals(Condition.FILES)) {
            var allFiles = Util.GetNamesOfFiles(selectedCategory.getAllFiles());
            switch (word) {
                case Alphabet.FILES_ALL:
                    return new Response(Answer.AnswerToAllFiles(allFiles), null);
                case Alphabet.FILES_GET:
                    condition = Condition.FILES_GET;
                    return new Response(Answer.AnswerToGetFile(allFiles), null);
                case Alphabet.FILES_SEND:
                    condition = Condition.FILES_SEND;
                    return new Response(Answer.SendFile, null);
            }
        } else if (condition.equals(Condition.FILES_GET)) {
            return handleFileGet(word);
        } else if (condition.equals(Condition.FILES_SEND)) {
            return handleFileSend(file, fileName);
        }
        return new Response(Answer.Idk, null);
    }

    private void mockRoomAndCat() {
        if (selectedRoom == null) {
            try {
                selectedRoom = handler.logInRoomByName("main228");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (selectedCategory == null) {
            try {
                selectedCategory = selectedRoom.getCategoryByName("mainCats228");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Response handleFileGet(String word) {
        try {
            var msgFile = selectedCategory.getFileByName(word);
            condition = Condition.FILES;
            return new Response(Answer.GetFileSuccess, msgFile.getFile());
        } catch (InvalidIdException e) {
            return new Response(Answer.GetFileNotFound, null);
        }
    }

    private Response handleFileSend(File file, String fileName) {
        if (file != null) {
            try {
                selectedCategory.addFile(file, fileName);
                condition = Condition.FILES;
                return new Response(Answer.SendFileSuccess, null);
            } catch (IOException e) {
                e.printStackTrace();
                return new Response(Answer.SendFileError, null);
            }
        }
        return new Response(Answer.SendFileNot, null);
    }
}
