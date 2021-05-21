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

    public Handler(String rootPath) {
        dbFileNames = new DBFileNames();
        rooms = new HashMap<>();
        this.rootPath = rootPath;
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

    public Room logInRoomById(String id) throws InvalidIdException {
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

    public Room logInRoomByName(String name) throws InvalidIdException {
        var id = dbFileNames.getIdByName(name);
        return logInRoomById(id);
    }

    public Room registerRoom(String name){
        var rootFile = new File(rootPath);
        if (!rootFile.exists())
            rootFile.mkdir();


        var newRoom = new Room(name, rootPath);
        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
    }

    public void clearAllInRoot(){
        var rootFile = new File(rootPath);

        deleteDirectory(rootFile, dbFileNames);
    }

    public ArrayList<Room> getAllRooms(){
        return new ArrayList<>(rooms.values());
    }

    private static boolean deleteDirectory(File directoryToBeDeleted, DBFileNames db) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file, db);
                try {
                    db.removeFileRecordById(file.getName());
                } catch (InvalidIdException e) {
                    continue;
                }
            }
        }
        return directoryToBeDeleted.delete();
    }

}
