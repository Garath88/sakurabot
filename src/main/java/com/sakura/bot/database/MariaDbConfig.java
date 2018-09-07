package com.sakura.bot.database;

import java.io.IOException;
import java.util.List;

import com.sakura.bot.utils.TxtReader;

final class MariaDbConfig {
    private final String dbUrl;
    private final String user;
    private final String pass;

    MariaDbConfig() throws IOException {
        List<String> list = TxtReader.readTxtFile("/configuration/mariadb.txt");
        dbUrl = list.get(0);
        user = list.get(1);
        pass = list.get(2);
    }

    String getDbUrl() {
        return dbUrl;
    }

    String getUser() {
        return user;
    }

    String getPass() {
        return pass;
    }
}
