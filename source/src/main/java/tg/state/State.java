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
    private final Handler handler;
    private Room selectedRoom;
    private Category selectedCategory;

    public State() {
        handler = new Handler("source/src/main/resources/Rooms");
        transition = new Transition().getTransitions();
        condition = Condition.BEGIN;
    }

    public Response transition(String word, java.io.File file, String fileName) {
        handler.restoreRooms();
        if (Util.BeginWith(word, '/')) {
            if (this.transition.containsKey(new Pair<>(this.condition, word))) {
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
        } else if (condition.equals(Condition.ROOMS)) {
            var allRooms = Util.GetNamesOfRooms(handler.getAllRooms());
            switch (word) {
                case Alphabet.ROOMS_ALL:
                    return new Response(Answer.AnswerToAllRooms(allRooms), null);
                case Alphabet.ROOMS_CHOOSE:
                    condition = Condition.ROOMS_CHOOSE;
                    return new Response(Answer.AnswerToRoomsChoose(allRooms), null);
                case Alphabet.ROOMS_CREATE:
                    condition = Condition.ROOMS_CREATE;
                    return new Response(Answer.RoomsCreate, null);
            }
        } else if (condition.equals(Condition.ROOMS_CHOOSE)) {
            return handleRoomChoose(word);
        } else if (condition.equals(Condition.ROOMS_CREATE)) {
            return handleRoomCreate(word);
        } else if (condition.equals(Condition.CATS)) {
            var allRooms = Util.GetNamesOfCats(selectedRoom.getAllCategories());
            switch (word) {
                case Alphabet.CATS_ALL:
                    return new Response(Answer.AnswerToAllCats(allRooms), null);
                case Alphabet.CATS_CHOOSE:
                    condition = Condition.CATS_CHOOSE;
                    return new Response(Answer.AnswerToCatsChoose(allRooms), null);
                case Alphabet.CATS_CREATE:
                    condition = Condition.CATS_CREATE;
                    return new Response(Answer.CatsCreate, null);
            }
        } else if (condition.equals(Condition.CATS_CHOOSE)) {
            return handleCategoryChoose(word);
        } else if (condition.equals(Condition.CATS_CREATE)) {
            return handleCategoryCreate(word);
        }
        return new Response(Answer.Idk, null);
    }

    private Response handleRoomChoose(String word) {
        try {
            selectedRoom = handler.logInRoomByName(word);
            condition = Condition.CATS;
            return new Response(Answer.Cats, null);
        } catch (InvalidIdException e) {
            return new Response(Answer.RoomNotFound, null);
        }
    }

    private Response handleRoomCreate(String word) {
        try {
            handler.registerRoom(word);
            condition = Condition.ROOMS;
            return new Response(Answer.RoomCreated, null);
        } catch (Exception e) {
            return new Response(Answer.RoomCreateFailed, null);
        }
    }

    private Response handleCategoryChoose(String word) {
        try {
            selectedCategory = selectedRoom.getCategoryByName(word);
            condition = Condition.FILES;
            return new Response(Answer.Files, null);
        } catch (Exception e) {
            return new Response(Answer.CatNotFound, null);
        }
    }

    private Response handleCategoryCreate(String word) {
        try {
            selectedRoom.addCategory(word);
            condition = Condition.CATS;
            return new Response(Answer.CatCreated, null);
        } catch (Exception e) {
            return new Response(Answer.CatCreateFailed, null);
        }
    }

    private Response handleFileGet(String word) {
        try {
            var msgFile = selectedCategory.getFileByName(word);
            condition = Condition.FILES;
            return new Response(Answer.GetFileSuccess, msgFile);
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
