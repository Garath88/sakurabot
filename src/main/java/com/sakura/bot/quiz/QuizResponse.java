package com.sakura.bot.quiz;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.utils.EmojiUtil;
import com.sakura.bot.utils.MessageUtil;
import com.sakura.bot.utils.RoleUtil;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class QuizResponse implements Response {
    @Override
    public void apply(Guild guild, MessageReceivedEvent e, EventWaiter waiter) {
        User user = e.getAuthor();
        String response = e.getMessage().getContentRaw().toLowerCase();
        if (response.toLowerCase().contains("asagi")) {
            MessageUtil.sendMessage(user, EmojiUtil.getCustomEmoji(e.getJDA(), "sakura"));
            MessageUtil.sendMessage(user, "- Correct");
            RoleUtil.addRole(guild, user, QuizQuestion.RULES_ROLE);
            RoleUtil.removeRole(guild, user, QuizQuestion.QUIZ_ROLE);

            RulesQuestion.perform(user, guild, waiter);
        } else {
            MessageUtil.sendMessage(user,
                String.format("- Aww.. wrong answer %s \n"
                        + "- You can try again by typing the **+member** command",
                    EmojiUtil.getCustomEmoji(e.getJDA(), "feelsbadman")));
            MessageUtil.sendMessage(guild.getOwner().getUser(),
                String.format("%s: %s", user.getAsMention(), response));
        }
    }
}
