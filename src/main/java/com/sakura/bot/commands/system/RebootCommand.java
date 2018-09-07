package com.sakura.bot.commands.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.Main;
import com.sakura.bot.Permissions;

public class RebootCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(RebootCommand.class);

    public RebootCommand() {
        this.name = "reboot";
        this.help = "restarts the bot.";
        this.guildOnly = false;
        this.requiredRoles = Permissions.MODERATOR.getValues();
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reactWarning();
        event.getJDA().shutdown();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.error("Reboot sleep failed", e);
            Thread.currentThread().interrupt();
        }
        Main.main(new String[0]);
    }
}
