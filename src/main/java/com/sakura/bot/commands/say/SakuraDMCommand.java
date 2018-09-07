package com.sakura.bot.commands.say;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.MessageUtil;

import net.dv8tion.jda.core.entities.User;

public class SakuraDMCommand extends Command {

    public SakuraDMCommand() {
        this.name = "sakura_dm";
        this.help = "say something with Sakura in a DM channel";
        this.arguments = "<text> followed by separator '|' <private channel id>.";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String message = event.getArgs();
            ArgumentChecker.checkArgsBySpace(message, 2);
            String[] items = message.split("\\|");
            User user = FinderUtil.findUsers(items[1]
                .replaceAll("\\s+", ""), event.getJDA()).stream()
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
            MessageUtil.sendMessage(user, items[0]);
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }
}
