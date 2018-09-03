package com.sakura.bot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.configuration.CommandList;

public interface Bot {
    EventWaiter getEventWaiter();

    void addCommands(CommandList commands);
}
