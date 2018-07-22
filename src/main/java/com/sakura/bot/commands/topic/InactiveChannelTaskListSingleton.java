package com.sakura.bot.commands.topic;

import com.sakura.bot.tasks.TaskListContainer;

public final class InactiveChannelTaskListSingleton {
    private static TaskListContainer taskListContainer;

    private InactiveChannelTaskListSingleton() {
    }

    public static synchronized TaskListContainer getInstance() {
        if (taskListContainer == null) {
            taskListContainer = new TaskListContainer();
        }
        return taskListContainer;
    }
}
