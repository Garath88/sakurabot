package com.sakura.bot;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.sakura.bot.commands.thread.InactiveThreadTaskList;
import com.sakura.bot.utils.CategoryUtil;
import com.sakura.bot.utils.EmojiUtil;
import com.sakura.database.ThreadDbTable;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class BotListener implements EventListener {
    private static final String SUCCESS_EMOJI = "sakura";
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
        } else if (event instanceof GuildMemberJoinEvent) {
            Member member = ((GuildMemberJoinEvent)event).getMember();
            member.getUser().openPrivateChannel()
                .queue(pc -> sendMessage(pc, "test"));
        }
    }

    private void setCustomEmojis(JDA jda) {
        String successEmoji = EmojiUtil.getCustomEmoji(jda, SUCCESS_EMOJI);
        if (StringUtils.isNotEmpty(successEmoji)) {
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

    private void sendMessage(MessageChannel chan, String message) {
        chan.sendMessage(message)
            .queue();
    }
}