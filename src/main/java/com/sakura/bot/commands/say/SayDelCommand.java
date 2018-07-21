package com.sakura.bot.commands.say;

import java.util.List;
import java.util.stream.Collectors;

import com.sakura.bot.Roles;
import com.sakura.bot.configuration.Config;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class SayDelCommand extends Command {
    public SayDelCommand() {
        this.name = "say_del";
        this.help = "deletes the last message sent";
        this.requiredRoles = Roles.MODERATOR.getValues();
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            TextChannel textChannel = SayChannelStorage.getChannel()
                .orElseThrow(IllegalArgumentException::new);
            List<Message> messages = textChannel.getIterableHistory().stream()
                .limit(30)
                .filter(msg -> msg.getAuthor().isBot()
                    && msg.getAuthor().getName().equals(Config.BOT_NAME))
                .collect(Collectors.toList());
            if (!messages.isEmpty()) {
                messages.get(0).delete()
                    .queue();
            }

        } catch (IllegalArgumentException e) {
            event.replyWarning(
                "You haven't added a text channel \n "
                    + "Please use the **" + Config.PREFIX + "say_add** command");
        }
    }
}
