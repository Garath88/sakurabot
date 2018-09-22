package com.sakura.bot;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.commands.thread.InactiveThreadTaskList;
import com.sakura.bot.commands.thread.SortThreads;
import com.sakura.bot.database.ThreadDbTable;
import com.sakura.bot.quiz.QuizQuestion;
import com.sakura.bot.utils.CategoryUtil;
import com.sakura.bot.utils.EmojiUtil;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
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
            allThreads.forEach(InactiveThreadTaskList::startInactivityTask);
            ThreadDbTable.checkForThreadDbInconsistency(allThreads);
            setCustomEmojis(jda);
            SortThreads.sortByPostCount(jda);
        } else if (event instanceof TextChannelDeleteEvent) {
            TextChannelDeleteEvent deletedChannel = (TextChannelDeleteEvent)event;
            ThreadDbTable.deleteChannel(deletedChannel.getChannel().getIdLong());
        } else if (event instanceof GuildMemberJoinEvent) {
            QuizQuestion.perform(event, waiter);
        } else if (event instanceof MessageReceivedEvent) {
            sortThreadsByPostCount(event);
        } else if (event instanceof MessageDeleteEvent) {
            sortThreadsByPostCount(event);
        }
        /*else if (event instanceof PrivateMessageReceivedEvent) {
            User user = ((PrivateMessageReceivedEvent)event).getAuthor();
            if (!user.isBot()) {
                User owner = GuildUtil.getGuild(event).getOwner().getUser();
                if (!user.getId().equals(owner.getId())) {
                    String message = ((PrivateMessageReceivedEvent)event).getMessage().getContentRaw();
                    MessageUtil.sendMessage(owner,
                        String.format("%s: %s", user.getAsMention(), message));
                }
            }
        }
        */
    }

    private void setCustomEmojis(JDA jda) {
        String successEmoji = EmojiUtil.getCustomEmoji(jda, SUCCESS_EMOJI);
        if (StringUtils.isNotEmpty(successEmoji)) {
            client.setSuccess(successEmoji);
        }
    }

    private void sortThreadsByPostCount(Event event) {
        JDA jda = event.getJDA();
        TextChannel textChan = ((GenericMessageEvent)event).getTextChannel();
        if (CategoryUtil.getThreadCategory(jda).getTextChannels().contains(textChan)) {
            SortThreads.sortByPostCount(jda);
        }
    }
}