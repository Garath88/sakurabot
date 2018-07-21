package com.sakura.bot.configuration;

import java.util.ArrayList;
import java.util.List;

import com.sakura.bot.commands.bump.BumpAddCommand;
import com.sakura.bot.commands.bump.BumpCommand;
import com.sakura.bot.commands.say.SayAddCommand;
import com.sakura.bot.commands.say.SayCommand;
import com.sakura.bot.commands.say.SayDelCommand;
import com.sakura.bot.commands.topic.TopicCommand;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jdautilities.examples.command.ShutdownCommand;

public class CommandList {

    private List<Command> commands = new ArrayList<>();

    public CommandList() {
        // adds commands
        //        client.addCommands(
        //            // command to show information about the bot
        //            /*
        //            new AboutCommand(Color.BLUE, "an example bot",
        //                new String[] { "Cool commands", "Nice examples", "Lots of fun!" },
        //                new Permission[] { Permission.ADMINISTRATOR }),
        //                */
        //
        //            // command to show a random cat
        //            //new CatCommand(),
        //
        //            // command to make a random choice
        //            //new ChooseCommand(),
        //
        //            // command to say hello
        //            new HelloCommand(waiter),
        //
        //
        // command to check bot latency
        commands.add(new PingCommand());

        // command to shut off the bot
        commands.add(new ShutdownCommand());

        commands.add(new BumpAddCommand());
        commands.add(new BumpCommand());
        commands.add(new TopicCommand());
        commands.add(new SayAddCommand());
        commands.add(new SayCommand());
        commands.add(new SayDelCommand());
    }

    public List<Command> getCommands() {
        return commands;
    }
}
