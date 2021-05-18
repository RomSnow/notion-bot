package tg.bot;

import java.util.function.Supplier;

public class BotToken implements Supplier<String> {
    private final String token;

    public BotToken(String token) {
        this.token = token;
    }

    @Override
    public String get() {
        return token;
    }
}
