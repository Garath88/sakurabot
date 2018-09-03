package com.sakura.bot;

import java.util.List;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.sakura.bot.commands.thread.InactiveThreadTaskList;
import com.sakura.bot.utils.CategoryUtil;
import com.sakura.database.ThreadDbTable;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class BotListener implements EventListener {
    private final CommandClientImpl client;

    BotListener(CommandClientImpl client) {
        this.client = client;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            List<TextChannel> allCustomChannels = CategoryUtil.getThreadCategory(event.getJDA())
                .getTextChannels();
            allCustomChannels.forEach(InactiveThreadTaskList::startInactivityTask);
            checkForCustomChannelDbInconsistency(allCustomChannels);
            setCustomEmojis(event.getJDA());
        } else if (event instanceof TextChannelDeleteEvent) {
            TextChannelDeleteEvent deletedChannel = (TextChannelDeleteEvent)event;
            ThreadDbTable.deleteChannel(deletedChannel.getChannel().getIdLong());
        }
    }

    private void setCustomEmojis(JDA jda) {
        final String successEmojiName = "sakura";
        String successEmojiId = jda.getEmotesByName(successEmojiName, false).stream()
            .findFirst()
            .map(ISnowflake::getId)
            .orElse(null);
        if (successEmojiId != null) {
            String successEmoji = String.format("<:%s:%s>", successEmojiName, successEmojiId);
            client.setSuccess(successEmoji);
        }
    }

    private void checkForCustomChannelDbInconsistency(List<TextChannel> allCustomChannels) {
        List<Long> allCustomChannelsById = allCustomChannels.stream()
            .map(ISnowflake::getIdLong)
            .collect(Collectors.toList());
        List<Long> deletedChannels = ThreadDbTable.getAllChannelIds().stream()
            .filter(chanId -> !allCustomChannelsById
                .contains(chanId))
            .collect(Collectors.toList());
        deletedChannels.forEach(ThreadDbTable::deleteChannel);
    }
}