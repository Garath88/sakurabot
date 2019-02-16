package com.sakura.bot;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.commands.copy.CopyMediaCommand;
import com.sakura.bot.commands.copy.CopyMessageChannelStorage;
import com.sakura.bot.commands.thread.InactiveThreadChecker;
import com.sakura.bot.commands.thread.SortThreads;
import com.sakura.bot.database.ThreadDbTable;
import com.sakura.bot.quiz.QuizQuestion;
import com.sakura.bot.utils.CategoryUtil;
import com.sakura.bot.utils.EmojiUtil;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class BotListener implements EventListener {
    private static final String SUCCESS_EMOJI = "sakura";
    private final CommandClientImpl client;
    private final EventWaiter waiter;

    BotListener(CommandClientImpl client, EventWaiter waiter) {
        this.client = client;
        this.waiter = waiter;
    }

    @Override
    public void onEvent(Event event) {
        JDA jda = event.getJDA();
        if (event instanceof ReadyEvent) {
            List<TextChannel> allThreads = CategoryUtil.getThreadCategory(jda)
                .getTextChannels();
            ThreadDbTable.checkForThreadDbInconsistency(allThreads);
            setCustomEmojis(jda);
            allThreads.forEach(thread ->
                SortThreads.countUniquePostsAndSort(thread, allThreads.size()));
            //Do this last else deleted channels might try to be sorted
            InactiveThreadChecker.startOrCancelInactivityTaskIfNotTopX(jda);
        } else if (event instanceof TextChannelDeleteEvent) {
            TextChannelDeleteEvent deletedChannelEvent = (TextChannelDeleteEvent)event;
            TextChannel deletedChannel = deletedChannelEvent.getChannel();
            ThreadDbTable.deleteChannel(deletedChannel.getIdLong());
            InactiveThreadChecker.cancelTaskIfDeleted(deletedChannel);
        } else if (event instanceof GuildMemberJoinEvent) {
            QuizQuestion.perform(event, waiter, client);
        } else if (event instanceof MessageReceivedEvent) {
            handleSortingOfThreads(event);
            handleMirrorChannel((MessageReceivedEvent)event);
        } else if (event instanceof MessageDeleteEvent) {
            handleSortingOfThreads(event);
        } else if (event instanceof GuildMemberLeaveEvent) {
            User user = ((GuildMemberLeaveEvent)event).getUser();
            waiter.removeWaitingTask(user);
        }
    }

    private void setCustomEmojis(JDA jda) {
        String successEmoji = EmojiUtil.getCustomEmoji(jda, SUCCESS_EMOJI);
        if (StringUtils.isNotEmpty(successEmoji)) {
            client.setSuccess(successEmoji);
        }
    }

    private void handleSortingOfThreads(Event event) {
        JDA jda = event.getJDA();
        TextChannel textChan = ((GenericMessageEvent)event).getTextChannel();
        if (CategoryUtil.getThreadCategory(jda).getTextChannels().contains(textChan)) {
            if (event instanceof MessageReceivedEvent) {
                SortThreads.countUniquePostsAndSort(textChan, 1);
            } else if (event instanceof MessageDeleteEvent) {
                MessageDeleteEvent deleteEvent = ((MessageDeleteEvent)event);
                ThreadDbTable.updateLatestMsgInDbIfDeleted(deleteEvent.getMessageIdLong(),
                    deleteEvent.getTextChannel());
            }
        }
    }

    private void handleMirrorChannel(MessageReceivedEvent event) {
        if (!event.getChannelType().equals(ChannelType.PRIVATE)) {
            String fromChanID = event.getTextChannel().getId();
            Map<String, String> channelIDs = CopyMessageChannelStorage.getChannelIDs();
            if (channelIDs.keySet().contains(fromChanID)) {
                Optional<TextChannel> channelToSendTo = event.getJDA().getTextChannels().stream()
                    .filter(chan -> chan.getId().equals(channelIDs.get(fromChanID)))
                    .findFirst();
                channelToSendTo.ifPresent(
                    textChannel -> CopyMediaCommand.sendMedia(event.getMessage(), textChannel));
            }
        }
    }
}