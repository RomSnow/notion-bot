package tg.bot;

import java.io.File;

public class Response {
    private final String text;
    private final File file;

    public Response(String text, File file) {
        this.text = text;
        this.file = file;
    }

    public String getText() {
        return text;
    }

    public File getFile() {
        return file;
    }
}
