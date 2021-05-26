package handler;

import db_storage.DBFileNames;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Handler {
    private Map<String, Room> rooms;
    private String rootPath;
    private final DBFileNames dbFileNames;
    private final UsageManager usageManager;

    public Handler(String rootPath) {
        dbFileNames = new DBFileNames();
        rooms = new HashMap<>();
        this.rootPath = rootPath;
        usageManager = UsageManager.getInstance();
        restoreRooms();
    }

    public void setRoot(String root) {
        rootPath = root;
    }

    public void restoreRooms() {
        var rootFile = new File(rootPath);
        var filePaths = rootFile.listFiles();

        if (filePaths != null) {
            for (var file : filePaths) {
                var fileName = file.getName();
                 if (rooms.containsKey(fileName)) {
                     var room = rooms.get(fileName);
                     room.update();
                     continue;
                 }

                Room room;
                try {
                    room = Room.restoreRoom(fileName, rootPath);
                } catch (InvalidIdException e) {
                    continue;
                }
                rooms.put(room.getId(), room);
            }
        }
    }

    public Room logInRoomById(String id) throws InvalidIdException {
        if (rooms.containsKey(id))
        {
            usageManager.tagFile(id);
            return rooms.get(id);
        }
        else {
            restoreRooms();

            if (rooms.containsKey(id))
                return rooms.get(id);
            else
                throw new InvalidIdException(id);
        }
    }

    public Room logInRoomByName(String name) throws InvalidIdException {
        var id = dbFileNames.getIdByName(name);
        return logInRoomById(id);
    }

    public Room registerRoom(String name) {
        var rootFile = new File(rootPath);
        if (!rootFile.exists())
            rootFile.mkdir();


        var newRoom = new Room(name, rootPath);
        rooms.put(newRoom.getId(), newRoom);
        usageManager.tagFile(newRoom.getId());
        return newRoom;
    }

    public void clearAllInRoot(){
        var rootFile = new File(rootPath);

        deleteDirectory(rootFile, dbFileNames);
    }

    public void removeRoomByName(String name) throws BusyException, InvalidIdException {
        var id = dbFileNames.getIdByName(name);
        removeRoomById(id);
    }

    public void removeRoomById(String id) throws BusyException {
        Deleter.tryToGetAccess(id, usageManager);
        var msg = rooms.get(id);
        var filePath = msg.getFilePath();
        var file = new File(filePath);
        Deleter.deleteDirectory(file, dbFileNames);
        rooms.remove(id);
    }

    public ArrayList<Room> getAllRooms(){
        return new ArrayList<>(rooms.values());
    }

    private static void deleteDirectory(File directoryToBeDeleted, DBFileNames db) {
        Deleter.deleteDirectory(directoryToBeDeleted, db);
    }

}
