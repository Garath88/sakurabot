package com.sakura.bot.commands.topic;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.TextChannel;

class CustomChannelContainer {
    private static List<TextChannel> channels = new ArrayList<>();

    private CustomChannelContainer() {
    }

    static List<TextChannel> getChannel() {
        return channels;
    }

    static void addChannel(TextChannel channel) {
        channels.add(channel);
    }
}
