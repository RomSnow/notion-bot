package handler;

import access_manager.InvalidPasswordException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class FileCreatorTests {
    private static Handler handler;

    @BeforeClass
    public static void setRoot() {
        handler = new Handler("src/test/Rooms");
        handler.setRoot("src/test/Rooms");
    }

    @Test
    public void testRegisterRoom() {
        var room = handler.registerRoom("TestRoom");
        Assert.assertEquals(room.getName(), "TestRoom");

        var id = room.getId();
        Assert.assertEquals("src/test/Rooms/%s".formatted(id), room.getFilePath());
    }


    @Test
    public void testRegisterAndLogRoom() throws InvalidIdException {
        var firstRoom = handler.registerRoom("First");
        var getFirstRoom = handler.logInRoomById(firstRoom.getId());

        Assert.assertEquals(firstRoom.getName(), getFirstRoom.getName());
    }

    @Test
    public void testAddCategory() {
        var room = handler.registerRoom("A");
        var category = room.addCategory("A1");

        Assert.assertEquals(room.getFilePath() + "/" + category.getId(), category.getFilePath());
    }

    @Test
    public void test() {
        var room = handler.registerRoom("MyRoom");
        var category1 = room.addCategory("First");
        var category2 = room.addCategory("Second");
    }

    @Test
    public void testWriteFileInCategory() throws IOException {
        var room = handler.registerRoom("Main");
        var category = room.addCategory("Second");

        var fileToAdd = createFile("Hello! This is test file...", "testFile");
        var fileMsg = category.addFile(fileToAdd);

        var newFile = fileMsg.getFile();
        var scanner = new Scanner(newFile);

        var text = scanner.nextLine();
        Assert.assertEquals(text, "Hello! This is test file...");
    }

    private File createFile(String fileSource, String fileName) throws IOException {
        var file = new File(fileName);
        var writer = new FileWriter(file);
        writer.write(fileSource);
        writer.close();
        return file;
    }

    @Test
    public void testRestore() throws InvalidIdException {
        var room = handler.registerRoom("restoreRoom");
        var cat = room.addCategory("restoreCat");
        cat.logOut();

        var restoreHandler = new Handler("src/test/Rooms");
        var oldRooms = handler.getAllRooms();
        var restoredRooms = restoreHandler.getAllRooms();

        var oldSet = new HashSet<String>();
        var resSet = new HashSet<String>();
        for (var old : oldRooms)
            oldSet.add(old.getId());

        for (var res : restoredRooms)
            resSet.add(res.getId());

        Assert.assertEquals(oldSet, resSet);

        Room tRoom = null;
        try {
            tRoom = restoreHandler.logInRoomByName("restoreRoom", "");
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        }
        var tCat = tRoom.addCategory("addedCategory");
        tCat.logOut();
        tRoom.logOut();

        handler.restoreRooms();
        for (var c : room.getAllCategories()) {
            if (c.getName().equals("addedCategory"))
                return;
        }
        Assert.fail();
    }

    @Test
    public void testRemove() throws InvalidIdException, BusyException {
        var room = handler.registerRoom("toRemove");
        var category = room.addCategory("catToRemove");
        category.logOut();
        room.removeCategoryByName("catToRemove");
        for (var cat : room.getAllCategories()) {
            if (cat.getName().equals("catToRemove"))
                Assert.fail();
        }
    }

    @Test
    public void testAccess() throws InvalidIdException, BusyException {
        var room = handler.registerRoom("accessRoom");
        try {
            handler.removeRoomByName("accessRoom");
            Assert.fail();
        } catch (BusyException ignore) {}
        room.logOut();

        handler.removeRoomByName("accessRoom");
    }

    @Test
    public void testPassword() throws InvalidIdException, InvalidPasswordException {
        var room = handler.registerRoom("AccessRoom");
        room.setPassword("pwd");
        room.logOut();

        var testRoom = handler.logInRoomByName("AccessRoom", "pwd");
        Assert.assertSame(room, testRoom);

        try {
            handler.logInRoomByName("AccessRoom", "invalid");
            Assert.fail();
        } catch (InvalidPasswordException ignored) {}

        var emptyRoom = handler.registerRoom("EmptyRoom");
        emptyRoom.logOut();

        handler.logInRoomByName("EmptyRoom", "");
    }

    @AfterClass
    public static void clear() {
        handler.clearAllInRoot();
    }

}
