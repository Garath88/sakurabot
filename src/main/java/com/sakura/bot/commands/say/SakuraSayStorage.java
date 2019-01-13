package com.sakura.bot.commands.say;

import java.util.Optional;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.TextChannel;

public final class SakuraSayStorage {
    private static boolean useDash = true;
    private static TextChannel channel = null;

    private SakuraSayStorage() {
    }

    public static Optional<TextChannel> getChannel() {
        return Optional.ofNullable(channel);
    }

    static void setChannel(TextChannel channel) {
        SakuraSayStorage.channel = channel;
    }

    static void toggleUseDash(CommandEvent event) {
        useDash = !useDash;
        if (useDash) {
            event.reply("I'm now using '-' when talking");
        } else {
            event.reply("I'm NOT using '-' when talking");
        }
    }

    static boolean getUseDash() {
        return useDash;
    }
}
