package com.sakura.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.User;

public final class ThreadDbTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadDbTable.class);
    private static final String DB_NAME = "threads";

    private ThreadDbTable() {
    }

    public static void addThread(User user, Channel customChannel) {
        String sql = String.format("INSERT INTO `sakurabot`.`%s` "
                + "(`user`,`user_id`, `name`, `id`) "
                + "VALUES ('%s', '%s', '%s', '%s')",
            DB_NAME, user.getName(), user.getId(), customChannel.getName(), customChannel.getId());
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

    public static List<Long> getAllChannelIds() {
        String sql = String.format("SELECT `id` FROM %s", DB_NAME);
        ResultSet result = MariaDbConnector.executeSql(sql);
        List<Long> channelIds = new ArrayList<>();
        if (result != null) {
            try {
                while (result.next()) {
                    channelIds.add(result.getLong("id"));
                }
                result.close();
                return channelIds;
            } catch (SQLException e) {
                LOGGER.error("Failed to close or get result from query", e);
            }
        }
        return Collections.emptyList();
    }

    public static ThreadInfo getCustomChannelInfo(User user) {
        String sql = String.format(
            "SELECT `name`, `id` FROM %s WHERE `user_id` = %s", DB_NAME, user.getId());
        ResultSet result = MariaDbConnector.executeSql(sql);
        List<String> outprint = new ArrayList<>();
        List<Long> channelIds = new ArrayList<>();
        int count = 0;
        if (result != null) {
            try {
                while (result.next()) {
                    count++;
                    outprint.add((String.format("`[%d]`  **%-2s**",
                        count, result.getString("name"))));
                    channelIds.add(result.getLong("id"));
                }
                result.close();
                return new ThreadInfo(outprint.stream()
                    .collect(Collectors.joining("\n")), outprint, channelIds);
            } catch (SQLException e) {
                LOGGER.error("Failed to close or get result from query", e);
            }
        }
        return new ThreadInfo("No created channels found!",
            Collections.emptyList(), Collections.emptyList());
    }

    public static void deleteChannel(Long id) {
        String sql = String.format(
            "DELETE FROM %s WHERE `id` = %s", DB_NAME, id);
        executeQuery(sql);
    }
}
