package com.sakura.bot.commands.say;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.MessageUtil;

import net.dv8tion.jda.core.entities.User;

public class SakuraDMCommand extends Command {

    public SakuraDMCommand() {
        this.name = "sakura_dm";
        this.help = "say something with Sakura in a DM to a user.";
        this.arguments = "<text> followed by separator '|' <user id>";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String args = event.getArgs();
            validateArguments(args);
            String[] items = args.split("\\|");
            String userId = items[1].replaceAll("\\s+", "");
            User user = FinderUtil.findUsers(userId, event.getJDA()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No user with that ID found!"));
            MessageUtil.sendMessage(user, items[0]);
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void validateArguments(String args) {
        ArgumentChecker.checkArgsByPipe(args, 2);
        String[] items = args.split("\\|");
        String userId = items[1].replaceAll("\\s+", "");
        Preconditions.checkArgument(StringUtils.isNumeric(userId),
            String.format("Invalid user id \"%s\", id must be numeric", userId));
    }
}
