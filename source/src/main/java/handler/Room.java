package handler;

import java.util.Collection;
import java.util.HashMap;

public class Room extends FileCreator {
    private final HashMap<String ,Category> categories;

    public Room(String name, String prePath) {
        super(name, prePath, FileType.Directory);
        categories = new HashMap<>();
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
        else
            categories.remove(id);
    }

    public Collection<Category> getAllCategories(){
        return categories.values();
    }
}
