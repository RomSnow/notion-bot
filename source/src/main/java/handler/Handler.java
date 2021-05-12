package handler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Handler {
    private static HashMap<String, Room> rooms = new HashMap<>();
    private static String rootPath = "src/main/resources/Rooms";

    public static void setRoot(String root) {
        rootPath = root;
    }

    public static void restoreRooms() {
        var rootFile = new File(rootPath);
        var filePaths = rootFile.listFiles();

        if (filePaths != null) {
            for (var file : filePaths) {

                if (rooms.containsKey(file.getName()))
                    continue;

                Room room;
                try {
                    room = Room.restoreRoom(file.getName(), rootPath);
                } catch (InvalidIdException e) {
                    continue;
                }
                rooms.put(room.getId(), room);
            }
        }
    }

    public static Room logInRoom(String id) throws InvalidIdException {
        if (rooms.containsKey(id))
            return rooms.get(id);
        else {
            restoreRooms();

            if (rooms.containsKey(id))
                return rooms.get(id);
            else
                throw new InvalidIdException(id);
        }
    }

    public static Room registerRoom(String name){
        var rootFile = new File(rootPath);
        if (!rootFile.exists())
            rootFile.mkdir();


        var newRoom = new Room(name, rootPath);
        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
    }

    public static void clearAllInRoot(){
        var rootFile = new File(rootPath);

        deleteDirectory(rootFile);
    }

    public static ArrayList<Room> getAllRooms(){
        return new ArrayList<>(rooms.values());
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}
