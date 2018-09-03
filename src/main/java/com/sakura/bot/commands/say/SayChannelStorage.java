package com.sakura.bot.commands.say;

import java.util.Optional;

import net.dv8tion.jda.core.entities.TextChannel;

public final class SayChannelStorage {
    private static TextChannel channel = null;

    private SayChannelStorage() {
    }

    public static Optional<TextChannel> getChannel() {
        return Optional.ofNullable(channel);
    }

    static void setChannel(TextChannel channel) {
        SayChannelStorage.channel = channel;
    }
}
