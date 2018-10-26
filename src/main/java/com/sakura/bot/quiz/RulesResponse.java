package com.sakura.bot.quiz;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.utils.MessageUtil;
import com.sakura.bot.utils.RoleUtil;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RulesResponse implements Response {

    @Override
    public void apply(Guild guild, MessageReceivedEvent e, EventWaiter waiter) {
        User user = e.getAuthor();
        String response = e.getMessage().getContentRaw().toLowerCase();
        if (response.equals("yes") || response.equals("y")) {
            RoleUtil.removeRole(guild, user, QuizQuestion.RULES_ROLE);
            MessageUtil.sendMessageToUser(user, "- Awesome! Welcome!");
        } else {
            RulesQuestion.perform(user, guild, waiter);
        }
    }
}
