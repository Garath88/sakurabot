package com.sakura.bot.commands.say;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.Roles;
import com.sakura.bot.configuration.Config;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public final class SayCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SayCommand.class);

    public SayCommand() {
        this.name = "say";
        this.help = "say something in a channel or with no arguments lists current talking channel.";
        this.arguments = "[<text>]";
        this.guildOnly = true;
        this.requiredRoles = Roles.MODERATOR.getValues();
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
        TextChannel textChannel = SayChannelStorage.getChannel()
            .orElseThrow(() -> new IllegalArgumentException("You haven't added a text channel to talk in! \n "
                + "Please use the **" + Config.PREFIX + "say_add** command"));
        if (StringUtils.isNotEmpty(message)) {
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
