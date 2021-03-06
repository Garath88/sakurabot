package com.sakura.bot.configuration;

import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.RebootCommand;

import commands.QuoteCommand;
import commands.channel.rp.RpCreateCommand;
import commands.channel.rp.RpDeleteCommand;
import commands.channel.thread.DebugThreadsCommand;
import commands.channel.thread.DeleteThreadCommand;
import commands.channel.thread.ThreadCommand;
import commands.copy.CopyMediaCommand;
import commands.misc.ChannelSpacingCommand;
import commands.quiz.MemberCommand;
import commands.say.DMCommand;
import commands.say.DMDeleteCommand;
import commands.say.ReadDMHistoryCommand;
import commands.say.SayCommand;
import commands.say.SayEditCommand;
import commands.say.SayEditLastCommand;
import commands.say.SetChanCommand;
import commands.system.HelpCommand;
import commands.system.PingCommand;
import commands.system.ShutdownCommand;
import commands.waifu.WaifuCommand;

public class CommandList {
    private static final String BOT_NAME = "Sakura";
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

        commands.add(new HelpCommand());
        commands.add(new PingCommand());
        commands.add(new ShutdownCommand());
        commands.add(new RebootCommand());
        commands.add(new ThreadCommand(waiter));
        commands.add(new RpCreateCommand(waiter));
        commands.add(new DeleteThreadCommand(waiter));
        commands.add(new RpDeleteCommand());
        commands.add(new SetChanCommand(BOT_NAME));
        commands.add(new SayCommand(BOT_NAME));
        commands.add(new SayEditCommand(BOT_NAME));
        commands.add(new SayEditLastCommand(BOT_NAME));
        commands.add(new DMCommand(BOT_NAME));
        commands.add(new DMDeleteCommand(BOT_NAME));
        commands.add(new ReadDMHistoryCommand(BOT_NAME));
        commands.add(new ChannelSpacingCommand());
        commands.add(new MemberCommand(waiter));
        commands.add(new CopyMediaCommand());
        commands.add(new WaifuCommand(waiter));
        commands.add(new QuoteCommand());
        commands.add(new DebugThreadsCommand());
    }

    public List<Command> getCommands() {
        return commands;
    }
}
