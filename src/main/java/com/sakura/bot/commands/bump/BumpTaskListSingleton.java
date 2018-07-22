package com.sakura.bot.commands.bump;

import com.sakura.bot.tasks.TaskListContainer;

final class BumpTaskListSingleton {
    private static TaskListContainer taskListContainer;

    private BumpTaskListSingleton() {
    }

    static synchronized TaskListContainer getInstance() {
        if (taskListContainer == null) {
            taskListContainer = new TaskListContainer();
        }
        return taskListContainer;
    }
}