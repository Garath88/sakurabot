package com.sakura.bot.commands.bump;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.command.CommandEvent;

final class BumpTaskListContainer {
    private static List<BumpTask> bumpTasks = new ArrayList<>();

    private BumpTaskListContainer() {
    }

    static void addBumpTask(String command, int time, CommandEvent event) {
        bumpTasks.add(new BumpTask(command, time, event));
    }

    static List<BumpTask> getBumpTaskList() {
        return bumpTasks;
    }

    static List<BumpTask> getQueuedTasks() {
        return bumpTasks
            .stream()
            .filter(BumpTask::isNotRunning)
            .collect(Collectors.toList());
    }
}
