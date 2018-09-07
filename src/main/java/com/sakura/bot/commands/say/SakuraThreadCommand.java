package com.sakura.bot.commands.say;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.Permissions;
import com.sakura.bot.commands.thread.ThreadCommand;
import com.sakura.bot.configuration.Config;
import com.sakura.bot.utils.ArgumentChecker;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

public class SakuraThreadCommand extends Command {

    public SakuraThreadCommand() {
        this.name = "sakura_thread";
        this.help = "Sakura creates a new thread.";
        this.arguments = "<topic>";
        this.requiredRoles = Permissions.MODERATOR.getValues();
        this.guildOnly = false;
        this.botPermissions = new Permission[] {
            Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String topic = event.getArgs();
            ArgumentChecker.checkArgsBySpace(topic, 1);
            createThread(event, topic);
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void createThread(CommandEvent event, String threadName) {
        TextChannel textChannel = SakuraChannelStorage.getChannel()
            .orElseThrow(() -> new IllegalArgumentException("You haven't added a text channel to talk in! \n "
                + "Please use the **" + Config.PREFIX + "sakura_set_chan** command"));
        ThreadCommand.createNewThread(event, threadName, false);
        SakuraSayCommand.sendMessage(String.format("**%s**", threadName), textChannel);
    }
}