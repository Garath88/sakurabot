package com.sakura.bot;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import commands.copy.CopyMediaCommand;
import commands.copy.CopyMessageChannelStorage;
import commands.quiz.QuizQuestion;
import commands.thread.InactiveThreadChecker;
import commands.thread.SortThreads;
import commands.thread.database.ThreadDbTable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import utils.CategoryUtil;
import utils.EmojiUtil;
import utils.MessageUtil;

public class BotListener implements EventListener {
    private static final String SUCCESS_EMOJI = "sakura";
    private final CommandClientImpl client;
    private final EventWaiter waiter;

    BotListener(CommandClientImpl client, EventWaiter waiter) {
        this.client = client;
        this.waiter = waiter;
    }

    @Override
    public void onEvent(GenericEvent event) {
        JDA jda = event.getJDA();
        if (event instanceof ReadyEvent) {
            List<TextChannel> allThreads = CategoryUtil.getThreadCategory(jda)
                .getTextChannels();
            ThreadDbTable.checkForThreadDbInconsistency(allThreads);
            setCustomEmojis(jda);
            allThreads.forEach(thread ->
                SortThreads.countUniquePostsAndSort(thread, allThreads.size()));
            //Do this last else deleted channels might try to be sorted
            InactiveThreadChecker.startOrCancelInactivityTaskIfNotTopX(allThreads);
        } else if (event instanceof TextChannelDeleteEvent) {
            TextChannelDeleteEvent deletedChannelEvent = (TextChannelDeleteEvent)event;
            TextChannel deletedChannel = deletedChannelEvent.getChannel();
            ThreadDbTable.deleteChannel(deletedChannel.getIdLong());
            InactiveThreadChecker.cancelTaskIfDeleted(deletedChannel);
        } else if (event instanceof GuildMemberJoinEvent) {
            QuizQuestion.perform(event, waiter, client);
        } else if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageRecievedEvent = (MessageReceivedEvent)event;
            handleSortingOfThreads(event);
            handleMirrorChannel(messageRecievedEvent);
            if (messageRecievedEvent.isFromType(ChannelType.PRIVATE)) {
                handlePrivateMessage(messageRecievedEvent);
            }
        } else if (event instanceof MessageDeleteEvent) {
            handleSortingOfThreads(event);
        } else if (event instanceof GuildMemberRemoveEvent) {
            User user = ((GuildMemberRemoveEvent)event).getUser();
            waiter.removeAllWaitingTasksForUser(user);
        }
    }

    private void setCustomEmojis(JDA jda) {
        String successEmoji = EmojiUtil.getCustomEmoji(jda, SUCCESS_EMOJI);
        if (StringUtils.isNotEmpty(successEmoji)) {
            client.setSuccess(successEmoji);
        }
    }

    private void handlePrivateMessage(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().toLowerCase().contains("thank")) {
            MessageUtil.sendMessageToUser(event.getAuthor(), "- You are welcome!", 1000);
        }
    }

    private void handleSortingOfThreads(GenericEvent event) {
        if (((GenericMessageEvent)event).getChannelType() == ChannelType.TEXT) {
            TextChannel textChan = ((GenericMessageEvent)event).getTextChannel();
            SortThreads.handleSortingOfThreads(event, textChan);
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