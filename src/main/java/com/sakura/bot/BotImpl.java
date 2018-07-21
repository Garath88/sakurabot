package com.sakura.bot;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sakura.bot.configuration.CommandList;
import com.sakura.bot.configuration.Config;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

public class BotImpl implements Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotImpl.class);

    private CommandClientBuilder client = new CommandClientBuilder();
    // define an eventwaiter, dont forget to add this to the JDABuilder!
    private EventWaiter waiter = new EventWaiter();
    private Config config;

    public BotImpl(Config config) {
        this.config = config;
        setupParameters();
    }

    private void setupParameters() {
        // The default is "Type !!help" (or whatver prefix you set)
        client.useDefaultGame();

        // sets the owner of the bot
        client.setOwnerId(config.getOwnerId());

        // sets emojis used throughout the bot on successes, warnings, and failures
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");

        // sets the bot prefix
        client.setPrefix(Config.PREFIX);
    }

    public void addCommands(CommandList commands) {
        commands.getCommands()
            .forEach(client::addCommands);
    }

    public void start() {
        // start getting a bot account set up
        try {
            new JDABuilder(AccountType.BOT)
                // set the token
                .setToken(config.getToken())

                // set the game for when the bot is loading
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing("loading..."))

                // add the listeners
                .addEventListener(waiter)
                .addEventListener(client.build())

                // start it up!
                .buildAsync();
        } catch (LoginException e) {
            LOGGER.error("Failed to start bot", e);
        }
    }
}




