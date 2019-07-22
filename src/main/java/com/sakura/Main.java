package com.sakura;

import com.sakura.bot.BotImpl;
import com.sakura.bot.configuration.CommandList;

public class Main {
    static {
        System.setProperty("logback.configurationFile", "./logback.xml");
    }
    public static void main(String[] args) {
        BotImpl bot = new BotImpl();
        CommandList commandList = new CommandList(bot.getEventWaiter());
        bot.addCommands(commandList);
        bot.start();
    }
}