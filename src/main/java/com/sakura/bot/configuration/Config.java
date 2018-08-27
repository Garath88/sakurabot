package com.sakura.bot.configuration;

import java.io.IOException;
import java.util.List;

import com.sakura.bot.utils.TxtReader;

public final class Config {
    public static final String PREFIX = "+";
    public static final String BOT_NAME = "Sakura";
    private final String token;
    private final String ownerId;

    public Config() throws IOException {
        List<String> list = TxtReader.readTxtFile("/configuration/config.txt");
        // config.txt contains two lines
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
