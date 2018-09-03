package com.sakura.bot;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.configuration.CommandList;
import com.sakura.bot.configuration.Config;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

public class BotImpl implements Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotImpl.class);
    private CommandClientBuilder client = new CommandClientBuilder();
    private EventWaiter waiter = new EventWaiter();

    public BotImpl() {
        setupParameters();
    }

    private void setupParameters() {
        // The default game is: playing Type [prefix]help
        client.useDefaultGame();

        // sets the owner of the bot
        client.setOwnerId(Config.getOwnerId());

        // sets emojis used throughout the bot on successes, warnings, and failures
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");

        // sets the bot prefix
        client.setPrefix(Config.PREFIX);
    }

    @Override
    public EventWaiter getEventWaiter() {
        return waiter;
    }

    @Override
    public void addCommands(CommandList commands) {
        commands.getCommands()
            .forEach(client::addCommands);
    }

    public void start() {
        // start getting a bot account set up
        try {
            init();
        } catch (LoginException e) {
            LOGGER.error("Failed to start bot", e);
        }
    }

    private void init() throws LoginException {
        CommandClient bot = client.build();
        new JDABuilder(AccountType.BOT)
            // set the token
            .setToken(Config.getToken())

            // set the game for when the bot is loading
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setGame(Game.playing("loading..."))

            // add the listeners
            .addEventListener(waiter)
            .addEventListener(bot)
            .addEventListener(new BotListener((CommandClientImpl)bot))

            // start it up!
            .buildAsync();
    }
}




