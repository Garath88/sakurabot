package com.sakura.bot.configuration;

import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.commands.ChannelSpacingCommand;
import com.sakura.bot.commands.MemberCommand;
import com.sakura.bot.commands.SpoilerCommand;
import com.sakura.bot.commands.say.SakuraDMCommand;
import com.sakura.bot.commands.say.SakuraSayCommand;
import com.sakura.bot.commands.say.SakuraSetChanCommand;
import com.sakura.bot.commands.say.SakuraThreadCommand;
import com.sakura.bot.commands.system.PingCommand;
import com.sakura.bot.commands.system.RebootCommand;
import com.sakura.bot.commands.system.ShutdownCommand;
import com.sakura.bot.commands.thread.DeleteThreadCommand;
import com.sakura.bot.commands.thread.ThreadCommand;

public class CommandList {

    private List<Command> commands = new ArrayList<>();

    public CommandList(EventWaiter waiter) {
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

        commands.add(new PingCommand());
        commands.add(new ShutdownCommand());
        commands.add(new ThreadCommand());
        commands.add(new DeleteThreadCommand(waiter));
        commands.add(new SpoilerCommand());
        commands.add(new SakuraSetChanCommand());
        commands.add(new SakuraSayCommand());
        commands.add(new SakuraThreadCommand());
        commands.add(new MemberCommand(waiter));
        commands.add(new SakuraDMCommand());
        commands.add(new RebootCommand());
        commands.add(new ChannelSpacingCommand());
    }

    public List<Command> getCommands() {
        return commands;
    }
}
