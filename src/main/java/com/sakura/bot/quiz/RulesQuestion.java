package com.sakura.bot.quiz;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.utils.MessageUtil;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

final class RulesQuestion {
    private RulesQuestion() {
    }

    static void perform(User user, Guild guild, EventWaiter waiter) {
        user.openPrivateChannel()
            .queue(pc -> pc.sendMessage(
                "- You should go and read the rules in #server info")
                .queue(msg2 -> pc.sendMessage(
                    "- Have you read the rules yet? **(y/n)**")
                    .queueAfter(2, TimeUnit.SECONDS)));
        MessageUtil.waitForResponse(user, guild, waiter, new RulesResponse());
    }
}
