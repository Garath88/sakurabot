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
        this.help = "Sakura says text and creates a new thread with specified topic.";
        this.arguments = "<text> followed by separator '|' <topic>";
        this.requiredRoles = Permissions.MODERATOR.getValues();
        this.guildOnly = false;
        this.botPermissions = new Permission[] {
            Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String message = event.getArgs();
            ArgumentChecker.checkArgsBySpace(message, 2);
            String[] items = message.split("\\|");
            createThread(event, items);
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void createThread(CommandEvent event, String[] message) {
        TextChannel textChannel = SakuraChannelStorage.getChannel()
            .orElseThrow(() -> new IllegalArgumentException("You haven't added a text channel to talk in! \n "
                + "Please use the **" + Config.PREFIX + "sakura_set_chan** command"));
        ThreadCommand.createNewThread(event, message[1].trim(), false);
        SakuraSayCommand.sendMessage(String.format("%s", message[0]), textChannel);
    }
}