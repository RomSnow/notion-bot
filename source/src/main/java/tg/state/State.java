package tg.state;

import handler.Category;
import handler.Handler;
import handler.InvalidIdException;
import handler.Room;
import org.javatuples.Pair;
import tg.bot.Response;
import util.Util;

import java.io.IOException;
import java.util.HashMap;

public class State {
    private Condition condition;
    private final HashMap<Pair<Condition, String>, Pair<Condition, String>> transition;
    private final HashMap<String, String> namesToId;
    private final HashMap<String, String> idToNames;
    private Room selectedRoom;
    private Category selectedCategory;

    public State() {
        transition = new Transition().getTransitions();
        condition = Condition.BEGIN;
        idToNames = new HashMap<>();
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
            var allFiles = Util.GetNamesOfFiles(selectedCategory.getAllFiles(), idToNames);
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
            try {
                var msgFile = selectedCategory.getFile(namesToId.get(word));
                condition = Condition.FILES;
                return new Response(Answer.GetFileSuccess, msgFile.getFile());
            } catch (InvalidIdException e) {
                return new Response(Answer.GetFileNotFound, null);
            }
        } else if (condition.equals(Condition.FILES_SEND)) {
            if (file != null) {
                try {
                    var msgFile = selectedCategory.addFile(file);
                    idToNames.put(msgFile.getId(), fileName);
                    namesToId.put(fileName, msgFile.getId());
                    condition = Condition.FILES;
                    return new Response(Answer.SendFileSuccess, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new Response(Answer.SendFileError, null);
                }
            }
            return new Response(Answer.SendFileNot, null);
        }
        return new Response(Answer.Idk, null);
    }

    private void mockRoomAndCat() {
        if (selectedRoom == null)
            selectedRoom = Handler.registerRoom("main");
        if (selectedCategory == null)
            selectedCategory = selectedRoom.addCategory("main");
    }
}
