package com.sakura.bot.commands.topic;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sakura.bot.Roles;
import com.sakura.bot.commands.say.SayCommand;
import com.sakura.bot.utils.ArgumentChecker;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.TextChannel;

public class TopicCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SayCommand.class);
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[^\\w ]");

    public TopicCommand() {
        this.name = "topic";
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
            validateInput(topic);
            Channel customChannel = createCustomChannel(event, topic);
            findAndAddCustomChannelToContainer(customChannel, topic);
        } catch (IllegalArgumentException | IllegalStateException e) {
            event.replyWarning(e.getMessage());
            LOGGER.error(e.getMessage());
        }
    }

    private void validateInput(String message) {
        if (StringUtils.isNotEmpty(message) && message.length() >= 2 && message.length() <= 100) {
            Matcher matcher = SYMBOL_PATTERN.matcher(message);
            if (matcher.find()) {
                throw new IllegalArgumentException("Invalid input, topic can not contain special character");
            }
        } else {
            throw new IllegalArgumentException("Topic can not be empty and must be between 2-100 characters");
        }
    }

    private Channel createCustomChannel(CommandEvent event, String topic) {
        List<net.dv8tion.jda.core.entities.Category> customCategory = FinderUtil.findCategories("CUSTOM", event.getGuild());
        Channel channel = event.getGuild().getController().createTextChannel(topic)
            .setTopic(topic)
            .setNSFW(true)
            .setParent(customCategory.get(0))
            .complete();
        event.reply(String.format("Succesfully created new channel: **%s**", topic));
        return channel;
    }

    private void findAndAddCustomChannelToContainer(Channel customChannel, String topic) {
        TextChannel customTextChannel = customChannel.getGuild().getTextChannels().stream()
            .filter(channel -> channel.getId().equals(customChannel.getId()))
            .findFirst().orElseThrow(() -> new IllegalArgumentException(String.format(
                "Something went really wrong, could not store created %s channel", topic)));
        CustomChannelContainer.addChannel(customTextChannel);
        customTextChannel.sendMessage("The topic has now been set to: " +
            String.format("**%s**", topic))
            .queue();
    }

}