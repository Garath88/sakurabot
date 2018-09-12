package com.sakura.bot.commands.thread;

import java.util.List;
import java.util.stream.Collectors;

import com.sakura.bot.tasks.TaskListContainer;

import net.dv8tion.jda.core.entities.TextChannel;

public final class InactiveThreadTaskList {
    private static TaskListContainer taskListContainer = new TaskListContainer();

    private InactiveThreadTaskList() {
    }

    static TaskListContainer getTaskListContainer() {
        return taskListContainer;
    }

    public static void startInactivityTask(TextChannel textChannel) {
        taskListContainer.addTask(new InactiveThreadCheckTask(textChannel));
        taskListContainer.scheduleTasks();
    }

    static List<InactiveThreadCheckTask> getTasks() {
        return taskListContainer.getTaskList()
            .stream()
            .map(task -> (InactiveThreadCheckTask)task)
            .collect(Collectors.toList());
    }
}
