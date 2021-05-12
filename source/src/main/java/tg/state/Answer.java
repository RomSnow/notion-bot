package tg.state;

public class Answer {
    public static final String Help = "This is help message!";
    public static final String Intro = "This is introduction message! Available commands: '/rooms', '/help'.";
    public static final String RoomsMock = "Now you have mock room 'main' and mock category 'main'. Type /goto to select this directory. Type /cancel to go back.";
    public static final String Idk = "I don't understand your message!";
    public static final String GoToMsg = "Now you in main room and main category. Type /send to download new file. Type /all to show names of all available files. Type /get to get file. Type /cancel to go back.";
    public static final String SendFile = "Send me one file. Type /cancel to go back.";
    public static final String SendFileSuccess = "Successfully send file.";
    public static final String SendFileError = "Error on send file.";
    public static final String SendFileNot = "I wait file from you.";
    public static final String GetFileSuccess = "Take this!";
    public static final String GetFileNotFound = "I can't find this file.";

    public static String AnswerToAllFiles(String files) {
        return "There is you have next files: " + files + ".";
    }

    public static String AnswerToGetFile(String files) {
        return "There is you have next files: " + files + ". Choose to get any file. Type /cancel to go back.";
    }
}
