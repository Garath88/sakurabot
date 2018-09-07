package com.sakura.bot.commands.say;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.Permissions;
import com.sakura.bot.configuration.Config;
import com.sakura.bot.utils.EmojiUtil;
import com.sakura.bot.utils.MentionUtil;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public final class SakuraSayCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SakuraSayCommand.class);

    public SakuraSayCommand() {
        this.name = "sakura_say";
        this.help = "say something with Sakura in a channel or with no arguments to list current talking channel.";
        this.arguments = "[<text>]";
        this.guildOnly = false;
        this.requiredRoles = Permissions.MODERATOR.getValues();
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String message = event.getArgs();
            say(event, message);
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());

        }
    }

    private void say(CommandEvent event, String message) {
        TextChannel textChannel = SakuraChannelStorage.getChannel()
            .orElseThrow(() -> new IllegalArgumentException("You haven't added a text channel to talk in! \n "
                + "Please use the **" + Config.PREFIX + "sakura_set_chan** command"));
        if (StringUtils.isNotEmpty(message)) {
            if (event.isFromType(ChannelType.PRIVATE)) {
                message = MentionUtil.addMentionsToMessage(event, message);
                message = EmojiUtil.addEmojisToMessage(event.getJDA(), message);
            }
            sendAttachments(event, textChannel);
            sendMessage(message, textChannel);
        } else {
            event.reply(String.format("Currently talking in channel: **%s**",
                textChannel.getName()));
        }
    }

    private static void sendAttachments(CommandEvent event, TextChannel textChannel) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        attachments.forEach(attachment -> {
            try {
                textChannel.sendFile(attachment.getInputStream(), attachment.getFileName())
                    .queue();
            } catch (IOException e) {
                LOGGER.error("Failed to add attachment", e);
            }
        });
    }

    static void sendMessage(String message, TextChannel textChannel) {
        if (!StringUtils.isEmpty(message)) {
            message = "- " + message;
            textChannel.sendMessage(message)
                .queue();
        }
    }
}
