package com.sakura.bot.commands.thread;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.Roles;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.CategoryUtil;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.TextChannel;

public class ThreadCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadCommand.class);
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[^\\w ]");

    public ThreadCommand() {
        this.name = "thread_add";
        this.help = "creates a new channel with topic";
        this.arguments = "<topic>";
        this.requiredRoles = Roles.FAN.getValues();
        this.botPermissions = new Permission[] {
            Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        addNewChannel(event);
    }

    private void addNewChannel(CommandEvent event) {
        String topic = event.getArgs();
        try {
            ArgumentChecker.checkIfArgsAreNotEmpty(topic);
            validateTopicName(topic);
            Channel customChannel = createCustomChannel(event, topic);
            TextChannel customTextChannel = sendTopicHasBeenSetMsg(customChannel, topic);
            InactiveChannelTaskList.startInactivityTask(customTextChannel);

        } catch (IllegalArgumentException | IllegalStateException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void validateTopicName(String topic) {
        if (StringUtils.isNotEmpty(topic) && topic.length() >= 2 && topic.length() <= 100) {
            Matcher matcher = SYMBOL_PATTERN.matcher(topic);
            if (matcher.find()) {
                throw new IllegalArgumentException("Invalid input, topic can not contain special character");
            }
        } else {
            throw new IllegalArgumentException("Topic can not be empty and must be between 2-100 characters");
        }
    }

    private Channel createCustomChannel(CommandEvent event, String topic) {
        Channel channel;
        net.dv8tion.jda.core.entities.Category customCategory = CategoryUtil.getCustomCategory(event.getJDA());
        validateChannelName(customCategory, topic);
        channel = event.getGuild().getController().createTextChannel(topic)
            .setTopic(topic)
            .setNSFW(true)
            .setParent(customCategory)
            .complete();
        event.reply(String.format("Succesfully created new channel: **%s**", topic));
        return channel;
    }

    private void validateChannelName(net.dv8tion.jda.core.entities.Category customCategory, String topic) {
        if (customCategory.getTextChannels().stream().anyMatch(chan -> chan.getName().equals(topic))) {
            throw new IllegalArgumentException(String.format(
                "This channel already exists! **%s**", topic));
        }
    }

    private TextChannel sendTopicHasBeenSetMsg(Channel customChannel, String topic) {
        TextChannel customTextChannel = customChannel.getGuild().getTextChannels().stream()
            .filter(channel -> channel.getId().equals(customChannel.getId()))
            .findFirst().orElseThrow(() -> {
                String errorMsg = String.format(
                    "Something went really wrong, could not store created %s channel", topic);
                LOGGER.error(errorMsg);
                return new IllegalStateException(errorMsg);
            });
        customTextChannel.sendMessage("The topic has now been set to: " +
            String.format("**%s**", topic))
            .queue();
        return customTextChannel;
    }
}