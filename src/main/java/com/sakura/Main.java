package com.sakura;

import java.io.IOException;

import com.sakura.bot.BotImpl;
import com.sakura.bot.configuration.CommandList;

public class Main {
    public static void main(String[] args) throws IOException {
        BotImpl bot = new BotImpl();
        CommandList commandList = new CommandList();
        bot.addCommands(commandList);
        bot.start();
    }
}
