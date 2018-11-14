package com.sakura.bot.commands.copy;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.MessageUtil;
import com.sakura.bot.utils.TextChannelUtil;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class CopyMediaCommand extends Command {
    private static final int MAX_HISTORY_LIMIT = 10;
    private TextChannel fromChannel;
    private TextChannel toChannel;

    public CopyMediaCommand() {
        this.name = "copy_media";
        this.help = "copies the attachements of one channel to another";
        this.arguments = "<from chan id> <to chan id> <starting msg id>";
        this.ownerCommand = true;
        this.botPermissions = new Permission[] {
            Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String[] items = event.getArgs().split("\\s");
            validateInput(items);
            fromChannel = TextChannelUtil.getChannel(items[0], event.getEvent());
            toChannel = TextChannelUtil.getChannel(items[1], event.getEvent());
            fromChannel.getMessageById(items[2]).queue(
                firstMsg -> {
                    checkMessageForPatternMatch(firstMsg.getContentRaw(), toChannel);
                    MessageUtil.sendAttachmentsToChannel(firstMsg.getAttachments(), toChannel);
                    postImages(items[2], MAX_HISTORY_LIMIT);
                }, fail -> event.replyWarning("Could not find first message"));

        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void validateInput(String[] args) {
        ArgumentChecker.checkArgsBySpace(Arrays.toString(args), 3);
        for (String item : args) {
            Preconditions.checkArgument(StringUtils.isNumeric(item),
                String.format("Invalid id \"%s\", id must be numeric", item));
        }
    }

    private void postImages(String msgId, int maxLimit) {
        fromChannel.getHistoryAfter(msgId, maxLimit).queue(
            history -> {
                List<Message> messages = history.getRetrievedHistory();
                if (!messages.isEmpty()) {
                    Iterator<Message> it = Lists.reverse(messages).iterator();
                    while (it.hasNext()) {
                        Message message = it.next();
                        sendMedia(message, toChannel);
                        if (!it.hasNext()) {
                            postImages(message.getId(), maxLimit);
                        }
                    }
                }
            }
        );
    }

    public static void sendMedia(Message message, TextChannel toChannel) {
        checkMessageForPatternMatch(message.getContentRaw(), toChannel);
        MessageUtil.sendAttachmentsToChannel(message.getAttachments(), toChannel);
    }

    private static void checkMessageForPatternMatch(String contentRaw, TextChannel toChannel) {
        MediaPatterns.getMediaPatterns().forEach(pattern ->
            sendMessageIfMatch(pattern.matcher(contentRaw), toChannel));
    }

    private static void sendMessageIfMatch(Matcher matcher, TextChannel toChannel) {
        if (matcher.find()) {
            toChannel.sendMessage(matcher.group(0))
                .queue();
        }
    }
}