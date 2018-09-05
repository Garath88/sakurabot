package com.sakura.bot.commands.say;

import java.util.Optional;

import net.dv8tion.jda.core.entities.TextChannel;

public final class SakuraChannelStorage {
    private static TextChannel channel = null;

    private SakuraChannelStorage() {
    }

    public static Optional<TextChannel> getChannel() {
        return Optional.ofNullable(channel);
    }

    static void setChannel(TextChannel channel) {
        SakuraChannelStorage.channel = channel;
    }
}
