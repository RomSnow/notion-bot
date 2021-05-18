package handler;

import db_storage.DBFileNames;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Room extends FileCreator {
    private final HashMap<String ,Category> categories;

    public static Room restoreRoom(String id, String prePath) throws InvalidIdException {
        var roomPath = prePath + File.separator + id;
        var listCategories = new ArrayList<Category>();

        var roomFile = new File(roomPath);

        if (!roomFile.isDirectory())
            throw new InvalidIdException(id);

        var filePaths = roomFile.listFiles();

        if (filePaths != null) {
            for (var file : filePaths) {
                var fileName = file.getName();
                var fileParent = file.getParent();
                listCategories.add(
                        Category.restoreCategory(file.getName(), file.getParent())
                );
            }
        }

        return new Room(id, prePath, listCategories);
    }

    public Room(String name, String prePath) {
        super(name, prePath, FileType.Directory);
        categories = new HashMap<>();
    }

    public Room(String id, String prePath, ArrayList<Category> categoryList) {
        super(prePath, id);
        categories = new HashMap<>();

        for (var category : categoryList)
            categories.put(category.getId(), category);
    }

    public Category addCategory(String name) {
        var category = new Category(name, this.getFilePath());
        categories.put(category.getId(), category);
        return category;
    }

    public Category getCategoryById(String id) throws InvalidIdException {
        if (!categories.containsKey(id))
            throw new InvalidIdException(id);
        else
            return categories.get(id);
    }

    public Category getCategoryByName(String name) throws InvalidIdException {
        var id = dbFileNames.getIdByName(name);
        return getCategoryById(id);
    }

    public void removeCategory(String id) throws InvalidIdException {
        if (!categories.containsKey(id))
            throw new InvalidIdException(id);
        else {
            var categoryPath = categories.get(id).getFilePath();
            deleteDirectory(new File(categoryPath), dbFileNames);
        }
    }

    public Collection<Category> getAllCategories(){
        return categories.values();
    }

    private static void deleteDirectory(File directoryToBeDeleted, DBFileNames db) {
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
        directoryToBeDeleted.delete();
    }
}
