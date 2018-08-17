package com.sakura.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.UriEncodingUtil;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

public class SpoilerCommand extends Command {
    public SpoilerCommand() {
        this.name = "spoiler";
        this.help = "If you want to say something that might be a spoiler";
        this.arguments = "<text>";
        this.botPermissions = new Permission[] {
            Permission.MESSAGE_MANAGE
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String arguments = event.getArgs();
            event.getMessage().delete()
                .queue();
            ArgumentChecker.checkIfArgsAreNotEmpty(arguments);
            String uriEncodedText = UriEncodingUtil.encodeURIComponent(arguments);
            String description = String.format("[Hover to view](https://dummyimage.com/600x400/000/fff&text=%s \"%s\")",
                uriEncodedText, arguments);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Spoiler")
                .setDescription(description);
            event.reply(builder.build());
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }
}