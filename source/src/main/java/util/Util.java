package util;

import handler.Category;
import handler.MessageFile;
import handler.Room;

import java.util.ArrayList;
import java.util.Collection;

public class Util {
    public static Boolean BeginWith(String in, char symbol) {
        return in != null && in.length() > 0 && in.charAt(0) == symbol;
    }

    public static String GetNamesOfFiles(ArrayList<MessageFile> files) {
        var result = new StringBuilder();
        files.forEach(f -> result.append(f.getName()).append(", "));
        if (result.length() < 3)
            return result.toString();
        return result.substring(0, result.length() - 2);
    }

    public static String GetNamesOfRooms(ArrayList<Room> rooms) {
        var result = new StringBuilder();
        rooms.forEach(r -> result.append(r.getName()).append(", "));
        if (result.length() < 3)
            return result.toString();
        return result.substring(0, result.length() - 2);
    }

    public static String GetNamesOfCats(Collection<Category> rooms) {
        var result = new StringBuilder();
        rooms.forEach(r -> result.append(r.getName()).append(", "));
        if (result.length() < 3)
            return result.toString();
        return result.substring(0, result.length() - 2);
    }
}
