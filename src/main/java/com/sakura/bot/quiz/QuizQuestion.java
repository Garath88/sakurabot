package com.sakura.bot.quiz;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.utils.MessageUtil;
import com.sakura.bot.utils.RoleUtil;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;

public final class QuizQuestion {
    public static final String QUIZ_ROLE = "Quiz";
    public static final String RULES_ROLE = "Rules";

    private QuizQuestion() {
    }

    public static void perform(Event event, EventWaiter waiter) {
        User user = ((GuildMemberJoinEvent)event).getMember().getUser();
        Guild guild = getGuild(event);
        RoleUtil.addRole(guild, user, QUIZ_ROLE);
        QuizResponse quizResponse = new QuizResponse();
        user.openPrivateChannel()
            .queue(pc -> pc.sendMessage(
                "*Yohoo~* it's Sakura! :heart:")
                .queue(msg -> pc.sendMessage(
                    "- In order to gain access to this lewd server you must first answer **one** simple **question!**")
                    .queueAfter(3, TimeUnit.SECONDS, msg2 -> pc.sendMessage(
                        "- Ready? ")
                        .queueAfter(3, TimeUnit.SECONDS, msg3 -> msg3.editMessage(
                            "- Ready? Great, lets' start!")
                            .queueAfter(1, TimeUnit.SECONDS, msg5 -> pc.sendMessage(
                                "- **Who gave me massive tits and an insane libido?**")
                                .queueAfter(2, TimeUnit.SECONDS, msg6 ->
                                    MessageUtil.waitForResponse(user, guild, waiter, quizResponse)))))));
    }

    private static Guild getGuild(Event event) {
        return event.getJDA().getGuilds().stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }
}
