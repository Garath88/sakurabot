package com.sakura.bot.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
            InputStream is = getClass().getResourceAsStream("/configuration/config.txt");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            list = readAllLines(br);
            br.close();
            isr.close();
            is.close();
        } catch (IOException | IllegalArgumentException e) {
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

    private static List<String> readAllLines(BufferedReader reader) throws IOException {
        List<String> result = new ArrayList<>();
        for (; ; ) {
            String line = reader.readLine();
            if (line == null)
                break;
            result.add(line);
        }
        return result;
    }
}
