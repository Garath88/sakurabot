package com.sakura.bot.quiz;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@FunctionalInterface
public interface Response {
    void apply(Guild guild, MessageReceivedEvent e, EventWaiter waiter);
}
