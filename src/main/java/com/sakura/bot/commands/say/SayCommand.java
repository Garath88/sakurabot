package com.sakura.bot.commands.say;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sakura.bot.Roles;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.configuration.Config;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public final class SayCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SayCommand.class);

    public SayCommand() {
        this.name = "say";
        this.help = "say something in a channel";
        this.arguments = "<text>";
        this.guildOnly = false;
        this.requiredRoles = Roles.MODERATOR.getValues();
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            TextChannel textChannel = SayChannelStorage.getChannel()
                .orElseThrow(IllegalArgumentException::new);
            sendAttachments(event, textChannel);
            sendMessage(event, textChannel);
        } catch (IllegalArgumentException e) {
            event.replyWarning(
                "You haven't added a text channel to talk in! \n "
                    + "Please use the **" + Config.PREFIX + "say_add** command");
        }
    }

    private void sendAttachments(CommandEvent event, TextChannel textChannel) {
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

    private void sendMessage(CommandEvent event, TextChannel textChannel) {
        String message = event.getArgs();
        if (!StringUtils.isEmpty(message)) {
            message = "- " + message;
            textChannel.sendMessage(message)
                .queue();
        }
    }
}
