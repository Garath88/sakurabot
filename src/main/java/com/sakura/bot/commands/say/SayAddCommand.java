package com.sakura.bot.commands.say;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.sakura.bot.Roles;
import com.sakura.bot.utils.ArgumentChecker;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.TextChannel;

public final class SayAddCommand extends Command {
    public SayAddCommand() {
        this.name = "say_add";
        this.help = "pick a channel to talk in";
        this.arguments = "<channel id>";
        this.requiredRoles = Roles.MODERATOR.getValues();
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String args = event.getArgs();
            validateInput(args);
            TextChannel channel = getChannel(args, event);
            SayChannelStorage.setChannel(channel);

            event.reply(
                String.format("Successfully added channel \"**%s**\" ", channel.getName()));
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void validateInput(String args) {
        ArgumentChecker.checkArgsBySpace(args, 1);
        Preconditions.checkArgument(StringUtils.isNumeric(args),
            String.format("Invalid channel id \"%s\", id must be numeric", args));
    }

    private TextChannel getChannel(String args, CommandEvent event) {
        List<TextChannel> textChannels = event.getJDA().getTextChannels();
        return textChannels.stream().filter(textChannel -> textChannel.getId().equals(args))
            .findFirst().orElseThrow(() -> new IllegalArgumentException(
                String.format("Could not find channel: %s", args)));
    }
}
