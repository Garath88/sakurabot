package com.sakura.bot.commands.say;

import java.util.Optional;

import net.dv8tion.jda.core.entities.TextChannel;

final class SayChannelStorage {
    private static TextChannel channel = null;

    private SayChannelStorage() {
    }

    static Optional<TextChannel> getChannel() {
        return Optional.ofNullable(channel);
    }

    static void setChannel(TextChannel channel) {
        SayChannelStorage.channel = channel;
    }
}
