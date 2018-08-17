package com.sakura.bot.commands.thread;

import com.sakura.bot.tasks.TaskListContainer;

import net.dv8tion.jda.core.entities.TextChannel;

public final class InactiveChannelTaskList {
    private static TaskListContainer taskListContainer = new TaskListContainer();

    private InactiveChannelTaskList() {
    }

    static TaskListContainer getTaskListContainer() {
        return taskListContainer;
    }

    public static void startInactivityTask(TextChannel textChannel) {
        taskListContainer.addTask(new InactiveChannelCheckTask(textChannel));
        taskListContainer.scheduleTasks();
    }
}
