package handler;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
    public void test(){
        var room = Handler.registerRoom("MyRoom");
        var category1 = room.addCategory("First");
        var category2 = room.addCategory("Second");
    }

    @AfterClass
    public static void clear() {
        Handler.clearAllInRoot();
    }

}
