package com.sakura.bot.quiz;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.utils.MessageUtil;
import com.sakura.bot.utils.PrivateChannelWrapper;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

final class RulesQuestion {
    private static final String RULES_CHANNEL = "server info";
    private static final int RULES_TIMEOUT_IN_MIN = 6;

    private RulesQuestion() {
    }

    static void perform(User user, Guild guild, EventWaiter waiter, CommandClient client) {
        TextChannel rulesChannel = guild.getTextChannelsByName(
            RULES_CHANNEL, true).stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        user.openPrivateChannel()
            .queueAfter(1, TimeUnit.SECONDS,
                PrivateChannelWrapper.userIsInGuild(pc -> pc.sendMessage(
                    String.format("- You should go and read the rules in %s",
                        rulesChannel.getAsMention()))
                    .queue(PrivateChannelWrapper.userIsInGuild(msg2 -> pc.sendMessage(
                        "- Have you read the rules? **(yes/no)**")
                            .queueAfter(5, TimeUnit.SECONDS,
                                listen -> MessageUtil.waitForResponse(user, guild, waiter,
                                    new RulesResponse(client), RULES_TIMEOUT_IN_MIN))),
                        fail -> {
                        })),
                fail -> {
                });

    }
}
