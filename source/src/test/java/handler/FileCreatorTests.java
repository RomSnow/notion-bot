package handler;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileCreatorTests {
    @BeforeClass
    public static void setRoot() {
        Handler.setRoot("src/test/resources/Rooms");
    }

    @Test
    public void testRegisterRoom() {
        var room = Handler.registerRoom("TestRoom");
        Assert.assertEquals(room.getName(), "TestRoom");

        var id = room.getId();
        Assert.assertEquals("src/test/resources/Rooms/%s".formatted(id), room.getFilePath());
    }

    @Test
    public void testRegisterAndLogRoom() throws InvalidIdException {
        var firstRoom = Handler.registerRoom("First");
        var getFirstRoom = Handler.logInRoom(firstRoom.getId());

        Assert.assertEquals(firstRoom.getName(), getFirstRoom.getName());
    }

    @Test
    public void testAddCategory() {
        var room = Handler.registerRoom("A");
        var category = room.addCategory("A1");

        Assert.assertEquals(room.getFilePath() + "/" + category.getId(), category.getFilePath());
    }

    @Test
    public void test() {
        var room = Handler.registerRoom("MyRoom");
        var category1 = room.addCategory("First");
        var category2 = room.addCategory("Second");
    }

    @Test
    public void testWriteFileInCategory() throws IOException {
        var room = Handler.registerRoom("Main");
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

    @AfterClass
    public static void clear() {
        Handler.clearAllInRoot();
    }

}
