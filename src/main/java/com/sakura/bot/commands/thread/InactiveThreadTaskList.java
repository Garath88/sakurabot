package com.sakura.bot.commands.thread;

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
}
