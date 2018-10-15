package com.sakura.bot.commands.say;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.GuildUtil;
import com.sakura.bot.utils.MessageUtil;

import net.dv8tion.jda.core.entities.User;

public class SakuraReadDMHistory extends Command {

    public SakuraReadDMHistory() {
        this.name = "sakura_dm_history";
        this.help = "reads Sakura DM history with a user.";
        this.arguments = "<user id> <number of messages>";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String args = event.getArgs();
            validateArguments(args);
            String[] items = args.split("\\s");
            User user = FinderUtil.findUsers(items[0]
                .replaceAll("\\s+", ""), event.getJDA()).stream()
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
            User owner = GuildUtil.getGuild(event.getJDA()).getOwner().getUser();
            user.openPrivateChannel()
                .queue(pc -> pc.getIterableHistory().limit(Integer.valueOf(items[1])).queue(
                    messages -> messages.forEach(msg -> MessageUtil.sendMessage(owner,
                        msg.getContentRaw())))
                );
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void validateArguments(String args) {
        ArgumentChecker.checkArgsBySpace(args, 2);
        String[] items = args.split("\\s");
        String userId = items[0];
        String limit = items[1];
        Preconditions.checkArgument(StringUtils.isNumeric(userId),
            String.format("Invalid user id \"%s\", id must be numeric", userId));
        Preconditions.checkArgument(StringUtils.isNumeric(limit),
            String.format("Invalid limit \"%s\", id must be numeric", limit));
    }
}