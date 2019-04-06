package com.sakura.bot.commands.system;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.utils.GuildUtil;
import com.sakura.bot.utils.RoleUtil;

import net.dv8tion.jda.core.entities.Role;

public class HelpCommand extends Command {

    public HelpCommand() {
        this.name = "help";
        this.help = "displays this help message.";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        Role quiz = RoleUtil.findRole(GuildUtil.getGuild(event.getJDA()), "Quiz");
        if (!RoleUtil.getMemberRoles(event).contains(quiz)) {
            event.getClient().displayHelp(event);
        } else {
            event.reply("- Tee hee~ nice try, but I've already told you where you can find the answer!");
        }
    }
}