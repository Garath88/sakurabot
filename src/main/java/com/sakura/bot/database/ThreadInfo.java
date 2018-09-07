package com.sakura.bot.database;

import java.util.List;

public class ThreadInfo {
    private String listedThreads;
    private List<String> threadNames;
    private List<Long> threadIds;

    ThreadInfo(String listedThreads, List<String> threadNames, List<Long> threadIds) {
        this.listedThreads = listedThreads;
        this.threadNames = threadNames;
        this.threadIds = threadIds;
    }

    public String getlistedChannels() {
        return listedThreads;
    }

    public List<String> getThreadNames() {
        return threadNames;
    }

    public List<Long> getThreadIds() {
        return threadIds;
    }
}
