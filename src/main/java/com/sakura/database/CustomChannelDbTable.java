package com.sakura.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.User;

public final class CustomChannelDbTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomChannelDbTable.class);

    private CustomChannelDbTable() {
    }

    public static void addChannel(User user, Channel customChannel) {
        String sql = String.format("INSERT INTO `sakurabot`.`custom_channel` "
                + "(`user`,`user_id`, `name`, `id`) "
                + "VALUES ('%s', '%s', '%s', '%s')",
            user.getName(), user.getId(), customChannel.getName(), customChannel.getId());
        executeQuery(sql);
    }

    private static void executeQuery(String sql) {
        try {
            ResultSet result = MariaDbConnector.executeSql(sql);
            if (result != null) {
                result.close();
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to close resource from query", e);
        }
    }

    public static String getChannels(User user) {
        String sql = String.format(
            "SELECT `name` FROM custom_channel WHERE `user_id` = %s", user.getId());
        ResultSet result = MariaDbConnector.executeSql(sql);
        List<String> outprint = new ArrayList<>();
        int count = 0;
        if (result != null) {
            outprint.add("Listing created channels: \n");
            try {
                while (result.next()) {
                    count++;
                    outprint.add((String.format("`[%d]`  **%-2s**",
                        count, result.getString("name"))));
                }
                result.close();
            } catch (SQLException e) {
                LOGGER.error("Failed to close or get result from query", e);
            }
            return outprint.stream()
                .collect(Collectors.joining("\n"));
        }
        return "No created channels found!";
    }

    public static void deleteChannel(Channel customChannel) {
        String sql = String.format(
            "DELETE FROM custom_channel WHERE `id` = %s", customChannel.getId());
        executeQuery(sql);
    }
}
