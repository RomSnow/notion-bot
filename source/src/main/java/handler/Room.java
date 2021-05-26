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
            for (var file : filePaths) {
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
        usageManager.tagFile(category.getId());
        return category;
    }

    public Category getCategoryById(String id) throws InvalidIdException {
        if (!categories.containsKey(id))
            throw new InvalidIdException(id);
        else {
            usageManager.tagFile(id);
            return categories.get(id);
        }
    }

    public Category getCategoryByName(String name) throws InvalidIdException {
        var id = dbFileNames.getIdByName(name);
        return getCategoryById(id);
    }

    public Collection<Category> getAllCategories(){
        return categories.values();
    }

    public void removeCategoryByName(String name) throws BusyException, InvalidIdException {
        var id = dbFileNames.getIdByName(name);
        removeCategoryById(id);
    }

    public void removeCategoryById(String id) throws BusyException {
        Deleter.tryToGetAccess(id, usageManager);
        var msg = categories.get(id);
        var filePath = msg.getFilePath();
        var file = new File(filePath);
        Deleter.deleteDirectory(file, dbFileNames);
        categories.remove(id);
    }

    public void update() {
        var filePaths = new File(getFilePath()).listFiles();

        if (filePaths != null) {
            for (var file : filePaths) {
                var fileName = file.getName();

                if (categories.containsKey(fileName)) {
                    var category = categories.get(fileName);
                    category.update();
                    continue;
                }

                Category restCat;
                try {
                    restCat = Category.restoreCategory(file.getName(), file.getParent());
                } catch (InvalidIdException e) {
                    continue;
                }

                categories.put(restCat.getId(), restCat);

            }
        }
    }
}
