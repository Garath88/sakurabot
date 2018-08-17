package com.sakura.bot.commands.bump;

import com.sakura.bot.tasks.TaskListContainer;

final class BumpTaskList {
    private static TaskListContainer taskListContainer = new TaskListContainer();

    private BumpTaskList() {
    }

    static TaskListContainer getTaskListContainer() {
        return taskListContainer;
    }
}