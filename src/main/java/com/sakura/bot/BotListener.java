package com.sakura.bot;

import com.sakura.bot.commands.thread.InactiveChannelTaskList;
import com.sakura.bot.utils.CategoryUtil;
import com.sakura.database.CustomChannelDbTable;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class BotListener implements EventListener {

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            CategoryUtil.getCustomCategory(event.getJDA())
                .getTextChannels()
                .forEach(InactiveChannelTaskList::startInactivityTask);
        } else if (event instanceof TextChannelDeleteEvent) {
            TextChannelDeleteEvent deletedChannel = (TextChannelDeleteEvent)event;
            CustomChannelDbTable.deleteChannel(deletedChannel.getChannel());
        }
    }
}
