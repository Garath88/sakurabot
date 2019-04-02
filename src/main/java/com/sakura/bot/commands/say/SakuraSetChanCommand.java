package com.sakura.bot.commands.say;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.Permissions;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.TextChannelUtil;

import net.dv8tion.jda.core.entities.TextChannel;

public final class SakuraSetChanCommand extends Command {
    public SakuraSetChanCommand() {
        this.name = "sakura_set_chan";
        this.help = "sets a channel where Sakura can talk in.";
        this.arguments = "<channel id>";
        this.requiredRoles = Permissions.MODERATOR.getValues();
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String args = event.getArgs();
            validateInput(args);
            TextChannel channel = TextChannelUtil.getChannel(args, event.getEvent());
            SakuraSayStorage.setChannel(channel.getId());

            event.reply(
                String.format("Now talking in channel: **%s** ", channel.getName()));
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void validateInput(String args) {
        ArgumentChecker.checkArgsBySpace(args, 1);
        Preconditions.checkArgument(StringUtils.isNumeric(args),
            String.format("Invalid channel id \"%s\", id must be numeric", args));
    }
}