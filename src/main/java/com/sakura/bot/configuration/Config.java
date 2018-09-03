package com.sakura.bot.configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.sakura.bot.utils.TxtReader;

public final class Config {
    /**
     * TODO
     * Fix this ugly mess
     **/
    public static final String PREFIX = "+";
    public static final String BOT_NAME = "Sakura";
    private static final String TOKEN = initToken();
    private static final String OWNER_ID = initOwnerId();

    private static String initToken() {
        List<String> list = Collections.emptyList();
        try {
            list = TxtReader.readTxtFile("/configuration/config.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list.get(0);
    }

    private static String initOwnerId() {
        List<String> list = Collections.emptyList();
        try {
            list = TxtReader.readTxtFile("/configuration/config.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list.get(1);
    }

    private Config() {
    }

    public static String getToken() {
        return TOKEN;
    }

    public static String getOwnerId() {
        return OWNER_ID;
    }
}
