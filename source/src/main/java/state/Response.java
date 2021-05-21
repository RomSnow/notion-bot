package state;

import handler.MessageFile;

public class Response {
    private final String text;
    private final MessageFile file;

    public Response(String text, MessageFile file) {
        this.text = text;
        this.file = file;
    }

    public String getText() {
        return text;
    }

    public MessageFile getFile() {
        return file;
    }
}
