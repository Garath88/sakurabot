package com.sakura.bot.utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.configuration.Config;
import com.sakura.bot.quiz.Response;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public final class MessageUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtil.class);

    private MessageUtil() {
    }

    public static void sendMessageToChannel(String message, MessageChannel channel, boolean usePrefix) {
        if (!StringUtils.isEmpty(message)) {
            if (usePrefix) {
                message = "- " + message;
            }
            MessageUtil.sendMessageToChannel(channel, message);
        }
    }

    public static void sendMessageToUser(User user, Message message) {
        user.openPrivateChannel()
            .queue(pc -> {
                if (GuildUtil.userIsInGuild(pc.getUser())) {
                    MessageUtil.sendMessageToChannel(pc, message);
                }
            });
    }

    public static void sendMessageToUser(User user, String message) {
        user.openPrivateChannel()
            .queue(pc -> {
                if (GuildUtil.userIsInGuild(pc.getUser())) {
                    MessageUtil.sendMessageToChannel(pc, message);
                }
            });
    }

    private static void sendMessageToChannel(MessageChannel channel, Message message) {
        sendMessageToChannel(channel, message.getContentRaw());
        List<Message.Attachment> attachments = message.getAttachments();
        if (!attachments.isEmpty()) {
            sendAttachmentsToChannel(attachments, channel);
        }
    }

    public static void sendAttachmentsToChannel(List<Attachment> attachments, MessageChannel channel) {
        attachments.forEach(attachment -> {
            try {
                channel.sendFile(attachment.getInputStream(), attachment.getFileName())
                    .queue();
            } catch (IOException e) {
                LOGGER.error("Failed to add attachment", e);
            }
        });
    }

    private static void sendMessageToChannel(MessageChannel channel, String message) {
        if (!message.isEmpty()) {
            channel.sendMessage(message)
                .queue(success -> {
                }, fail -> {
                });
        }
    }

    public static void waitForResponse(User user, Guild guild,
        EventWaiter waiter, Response checkResponse, int timeoutMinutes) {
        waiter.waitForEvent(MessageReceivedEvent.class,
            // make sure it's by the same user, and in the same channel
            e -> e.getAuthor().equals(user) && e.getChannel().getType().equals(ChannelType.PRIVATE),
            // respond, inserting the name they listed into the response
            e -> checkResponse.apply(guild, e, waiter),
            timeoutMinutes, TimeUnit.MINUTES, () -> MessageUtil.sendMessageToUser(user, String.format("- Sorry you were too slow %s :frowning: \n"
                    + "- Please try again by typing the **%s" + "member** command.",
                user.getAsMention(), Config.PREFIX)));
    }
}
