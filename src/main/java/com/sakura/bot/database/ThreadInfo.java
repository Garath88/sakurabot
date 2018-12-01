package com.sakura.bot.database;

import java.util.List;

public class ThreadInfo {
    private String listedThreads;
    private List<Long> threadIds;

    ThreadInfo(String listedThreads, List<Long> threadIds) {
        this.listedThreads = listedThreads;
        this.threadIds = threadIds;
    }

    public String getlistedChannels() {
        return listedThreads;
    }

    public List<Long> getThreadIds() {
        return threadIds;
    }
}
