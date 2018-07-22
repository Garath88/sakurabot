package com.sakura;

import java.io.IOException;

import com.sakura.bot.BotImpl;
import com.sakura.bot.configuration.CommandList;
import com.sakura.bot.configuration.Config;

public class Main {
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        BotImpl bot = new BotImpl(config);
        CommandList commandList = new CommandList();
        bot.addCommands(commandList);
        bot.start();
    }
}
