package com.sakura.bot.quiz;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.utils.GuildUtil;
import com.sakura.bot.utils.MessageUtil;
import com.sakura.bot.utils.PrivateChannelWrapper;
import com.sakura.bot.utils.RoleUtil;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;

public final class QuizQuestion {
    public static final String QUIZ_QUESTION =
        "- **Who do I \"fight to the death\" in order to free myself and my sister in Taimanin Asagi 1?** *(As seen in OVA 1 episodes 1-4 or VN 1)*";
    public static final String QUIZ_ROLE = "Quiz";
    public static final String RULES_ROLE = "Rules";
    public static final int QUIZ_TIMEOUT_IN_MIN = 10;

    private QuizQuestion() {
    }

    public static void perform(Event event, EventWaiter waiter, CommandClientImpl client) {
        User user = ((GuildMemberJoinEvent)event).getMember().getUser();
        if (!user.isBot()) {
            Guild guild = GuildUtil.getGuild(event.getJDA());
            RoleUtil.addRole(guild, user, QUIZ_ROLE);
            user.openPrivateChannel()
                .queue(PrivateChannelWrapper.userIsInGuild(pc ->
                    pc.sendMessage("*Yohoo~* it's Sakura! :heart:")
                        .queue(PrivateChannelWrapper.userIsInGuild(msg2 ->
                            pc.sendMessage("- In order to gain access to this lewd server you must first answer **one** simple **question!**")
                                .queueAfter(3, TimeUnit.SECONDS, PrivateChannelWrapper.userIsInGuild(msg3 ->
                                    pc.sendMessage("- Ready? ")
                                        .queueAfter(3, TimeUnit.SECONDS, PrivateChannelWrapper.userIsInGuild(msg4 ->
                                            msg4.editMessage("- Ready? Great, let's start!")
                                                .queueAfter(1, TimeUnit.SECONDS, PrivateChannelWrapper.userIsInGuild(msg5 ->
                                                    pc.sendMessage(QUIZ_QUESTION)
                                                        .queueAfter(3, TimeUnit.SECONDS,
                                                            listen -> MessageUtil.waitForResponseInDM(user, guild, waiter,
                                                                new QuizResponse(client), QuizQuestion.QUIZ_TIMEOUT_IN_MIN,
                                                                QuizResponse.RETRY_MSG),
                                                            fail -> {
                                                            })), fail -> {
                                                })), fail -> {
                                        })), fail -> {
                                })), fail -> {
                        })), fail -> {
                });
        }
    }
}
