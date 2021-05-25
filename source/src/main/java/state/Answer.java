package state;

public class Answer {
    public static final String Help = "This is help message!";
    public static final String Intro = "This is introduction message! Available commands: /rooms, /help.";
    public static final String Rooms = "Now you in the room section. Here you can type: /roomAll to get list of rooms, /roomCreate to create new one, /roomChoose to select room and go to category section, /cancel to go back.";
    public static final String RoomsCreate = "Type name for new room or /cancel to go back.";
    public static final String CatsCreate = "Type name for new category or /cancel to go back.";
    public static final String Idk = "I don't understand your message!";
    public static final String GoToMsg = "Now you in main room and main category. Type /send to download new file. Type /all to show names of all available files. Type /get to get file. Type /cancel to go back.";
    public static final String SendFile = "Send me one file. Type /cancel to go back.";
    public static final String SendFileSuccess = "Successfully send file.";
    public static final String SendFileError = "Error on send file.";
    public static final String SendFileNot = "I wait file from you.";
    public static final String GetFileSuccess = "Take this!";
    public static final String GetFileNotFound = "I can't find this file.";
    public static final String RoomCreated = "Successfully create new room.";
    public static final String CatCreated = "Successfully create new category.";
    public static final String RoomNotFound = "There is no room with this name.";
    public static final String CatNotFound = "There is no category with this name.";
    public static final String RoomCreateFailed = "Failed to create new room.";
    public static final String CatCreateFailed = "Failed to create new category.";
    public static final String Files = "Now you in the files section. Here you can type: /all to get list of files in this category, /send to send me your file, /get to get your own file, /cancel to go back.";
    public static final String Cats = "Now you in the category section. Here you can type: /catAll to get list of categories, /catCreate to create new one, /catChoose to select category and manage files, /cancel to go back.";
    public static final String ErrWhileSendingFile = "An error occurred while sending file";

    public static String AnswerToAllFiles(String files) {
        return "There is you have next files: " + files + ".";
    }

    public static String AnswerToGetFile(String files) {
        return "There is you have next files: " + files + ". Choose to get any file. Type /cancel to go back.";
    }

    public static String AnswerToAllRooms(String rooms) {
        return "There is you have next rooms: " + rooms + ".";
    }

    public static String AnswerToRoomsChoose(String rooms) {
        return "There is you have next rooms: " + rooms + ". Choose one to select room. Type /cancel to go back.";
    }

    public static String AnswerToAllCats(String cats) {
        return "There is you have next categories: " + cats + ".";
    }

    public static String AnswerToCatsChoose(String cats) {
        return "There is you have next categories: " + cats + ". Choose one to select room. Type /cancel to go back.";
    }
}
