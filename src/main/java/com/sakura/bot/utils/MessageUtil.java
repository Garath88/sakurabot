package com.sakura.bot.utils;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.configuration.Config;
import com.sakura.bot.quiz.Response;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public final class MessageUtil {
    private MessageUtil() {
    }

    public static void sendMessage(User user, String message) {
        user.openPrivateChannel()
            .queue(pc -> MessageUtil.sendMessage(pc, message));
    }

    public static void sendMessage(MessageChannel chan, String message) {
        chan.sendMessage(message)
            .queue();
    }

    public static void waitForResponse(User user, Guild guild,
        EventWaiter waiter, Response checkResponse, int timeoutMinutes) {
        waiter.waitForEvent(MessageReceivedEvent.class,
            // make sure it's by the same user, and in the same channel
            e -> e.getAuthor().equals(user) && e.getChannel().getType().equals(ChannelType.PRIVATE),
            // respond, inserting the name they listed into the response
            e -> checkResponse.apply(guild, e, waiter),
            // if the user takes more than a minute, time out
            timeoutMinutes, TimeUnit.MINUTES, () -> user.openPrivateChannel()
                .queue(pc -> MessageUtil.sendMessage(pc, String.format("- Sorry you were too slow %s :frowning: \n"
                        + "- Please try again by typing the **%s" + "member** command.",
                    user.getAsMention(), Config.PREFIX))));
    }
}