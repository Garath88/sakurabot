package com.sakura.bot.commands.thread;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.database.CustomChannelDbTable;

import net.dv8tion.jda.core.Permission;

public class ThreadDelCommand extends Command {
    public ThreadDelCommand() {
        this.name = "thread_del";
        this.help = "deletes a created channel";
        this.botPermissions = new Permission[] {
            Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(CustomChannelDbTable.getChannels(event.getMember().getUser()));
    }
}
