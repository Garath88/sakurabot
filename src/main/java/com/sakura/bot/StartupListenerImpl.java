package com.sakura.bot;

import com.sakura.bot.commands.thread.InactiveChannelTaskList;
import com.sakura.bot.utils.CategoryUtil;

import net.dv8tion.jda.core.events.Event;

public class StartupListenerImpl implements StartupListener {
    @Override
    public void runStartupCommands(Event event) {
        CategoryUtil.getCustomCategory(event.getJDA())
            .getTextChannels()
            .forEach(InactiveChannelTaskList::startInactivityTask);
    }
}