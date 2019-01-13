package com.sakura.bot.commands.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.Permissions;
import com.sakura.bot.utils.ArgumentChecker;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

public class ChannelSpacingCommand extends Command {
    public ChannelSpacingCommand() {
        this.name = "channels";
        this.help = "if you wish to have spaces in your channel names.";
        this.arguments = "<space|dash>";
        this.requiredRoles = Permissions.MODERATOR.getValues();
        this.botPermissions = new Permission[] {
            Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String arguments = event.getArgs();
            ArgumentChecker.checkArgsBySpace(arguments, 1);
            List<TextChannel> textChannels = event.getGuild()
                .getTextChannels();
            if (arguments.equals("dash")) {
                String spacedChannels = spaceOrDashChannels(textChannels, " ", "-");
                event.reply(String.format("Dashed channels: %s", spacedChannels));
            } else if (arguments.equals("space")) {
                String dashedChannels = spaceOrDashChannels(textChannels, "-", "` `");
                event.reply(String.format("Spaced channels: %s", dashedChannels));
            }
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private String spaceOrDashChannels(List<TextChannel> channels, String from, String to) {
        List<String> channelNames = new ArrayList<>();
        channels.forEach(channel -> {
            if (channel.getName().contains(from)) {
                channelNames.add(String.format("**%s**", channel.getName()));
                String newName = String.join(to, channel.getName().split(from));
                channel.getManager().setName(newName)
                    .queue();
            }
        });
        return channelNames.stream()
            .collect(Collectors.joining(", "));
    }
}