package com.sakura.bot.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Config {
    public static final String PREFIX = "+";
    public static final String BOT_NAME = "Sakura";
    private final String token;
    private final String ownerId;

    public Config() throws IOException {
        // config.txt contains two lines
        List<String> list;
        try {
            list = Files.readAllLines(Paths.get("config.txt"));
        } catch (IOException e) {
            throw new IOException("Failed to load configuration", e);
        }

        // the first is the bot token
        token = list.get(0);

        // the second is the bot's owner's id
        ownerId = list.get(1);
    }

    public String getToken() {
        return token;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
