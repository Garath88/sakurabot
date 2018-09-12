package com.sakura.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.quiz.QuizQuestion;
import com.sakura.bot.quiz.QuizResponse;
import com.sakura.bot.utils.MessageUtil;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class MemberCommand extends Command {
    private final EventWaiter waiter;

    public MemberCommand(EventWaiter waiter) {
        this.name = "member";
        this.help = "Answer a question for getting the member role";
        this.guildOnly = false;
        this.waiter = waiter;
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        Guild guild = event.getJDA().getGuilds().stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        Member member = FinderUtil.findMembers(event.getAuthor().getId(), guild)
            .stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        if (!member.getRoles().isEmpty() && event.getChannelType().equals(ChannelType.PRIVATE)) {
            event.reply(QuizQuestion.QUIZ_QUESTION);
            MessageUtil.waitForResponse(event.getAuthor(), guild, waiter,
                new QuizResponse(), QuizQuestion.QUIZ_TIMEOUT_IN_MIN);
        }
    }
}