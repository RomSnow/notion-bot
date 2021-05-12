package handler;

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
            for (var file : filePaths)
                listCategories.add(
                        Category.restoreCategory(file.getName(), file.getParent())
                );
        }

        return new Room("category", id, prePath, listCategories);
    }

    public Room(String name, String prePath) {
        super(name, prePath, FileType.Directory);
        categories = new HashMap<>();
    }

    public Room(String name, String id, String prePath, ArrayList<Category> categoryList) {
        super(name, prePath, id);
        categories = new HashMap<>();

        for (var category : categoryList)
            categories.put(category.getId(), category);
    }

    public Category addCategory(String name) {
        var category = new Category(name, this.getFilePath());
        categories.put(category.getId(), category);
        return category;
    }

    public Category getCategory(String id) throws InvalidIdException {
        if (!categories.containsKey(id))
            throw new InvalidIdException(id);
        else
            return categories.get(id);
    }

    public void removeCategory(String id) throws InvalidIdException {
        if (!categories.containsKey(id))
            throw new InvalidIdException(id);
        else {
            var categoryPath = categories.get(id).getFilePath();
            deleteDirectory(new File(categoryPath));
        }
    }

    public Collection<Category> getAllCategories(){
        return categories.values();
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
