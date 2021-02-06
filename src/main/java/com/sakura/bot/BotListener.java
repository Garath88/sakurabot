package com.sakura.bot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import commands.channel.database.RpChannelsDbTable;
import commands.channel.database.ThreadsDbTable;
import commands.channel.thread.InactiveThreadChecker;
import commands.channel.thread.SortThreads;
import commands.copy.CopyMediaCommand;
import commands.copy.CopyMessageChannelStorage;
import commands.quiz.QuizQuestion;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
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
    private static final List<String>
        selfRoles = Arrays.asList("Moonrunes", "Archive", "Roleplayer");

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
            ThreadsDbTable.checkForThreadDbInconsistency(allThreads);
            setCustomEmojis(jda);
            allThreads.forEach(thread ->
                SortThreads.countUniquePostsAndSort(thread, allThreads.size()));
            //Do this last else deleted channels might try to be sorted
            InactiveThreadChecker.startOrCancelInactivityTaskIfNotTopX(allThreads);
        } else if (event instanceof TextChannelDeleteEvent) {
            TextChannelDeleteEvent deletedChannelEvent = (TextChannelDeleteEvent)event;
            TextChannel deletedChannel = deletedChannelEvent.getChannel();
            ThreadsDbTable.deleteChannel(deletedChannel.getIdLong());
            InactiveThreadChecker.cancelTaskIfDeleted(deletedChannel);
            RpChannelsDbTable.deleteChannel(deletedChannel.getIdLong());
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
        } else if (event instanceof GuildMemberRoleAddEvent) {
            GuildMemberRoleAddEvent guildMemberRoleAddEvent = (GuildMemberRoleAddEvent)event;
            handleSelfReactionRoles(guildMemberRoleAddEvent);
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

    private void handleSelfReactionRoles(GuildMemberRoleAddEvent guildMemberRoleAddevent) {
        List<String> roles = guildMemberRoleAddevent.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());
        if (selfRoles.containsAll(roles)) {
            String message = String.format("- You've recieved the **%s** role!%n", roles);
            if (roles.contains("Moonrunes")) {
                message += "- You now have access to the <#784165489739825252> channel.";
            } else if (roles.contains("Roleplayer")) {
                message += "- You now have access to the <#554806001619173377> channel as well as other RP channels.";
            } else if (roles.contains("Archive")) {
                message += "- You now have access to the **Public Archive** "
                    + "which is located at the bottom of the channel list";
            }
            MessageUtil.sendMessageToUser(guildMemberRoleAddevent.getUser(), message);
        }
    }
}