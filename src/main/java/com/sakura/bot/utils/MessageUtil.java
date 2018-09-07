package com.sakura.bot.utils;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

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

}
