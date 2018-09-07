package com.sakura.bot;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.configuration.Config;
import com.sakura.bot.utils.EmojiUtil;
import com.sakura.bot.utils.MessageUtil;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

public final class Quiz {
    private static final int QUIZ_TIMEOUT_IN_MIN = 10;

    private Quiz() {
    }

    static void perform(Event event, EventWaiter waiter) {
        User user = ((GuildMemberJoinEvent)event).getMember().getUser();
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
                                    waitForResponse(user, event, waiter)))))));
    }

    public static void waitForResponse(User user, Event event, EventWaiter waiter) {
        waiter.waitForEvent(MessageReceivedEvent.class,
            // make sure it's by the same user, and in the same channel
            e -> e.getAuthor().equals(user) && e.getChannel().getType().equals(ChannelType.PRIVATE),
            // respond, inserting the name they listed into the response
            e -> checkResponse(event, e, waiter),
            // if the user takes more than a minute, time out
            QUIZ_TIMEOUT_IN_MIN, TimeUnit.MINUTES, () -> user.openPrivateChannel()
                .queue(pc -> MessageUtil.sendMessage(pc, String.format("- Sorry you were too slow %s :frowning: \n"
                        + "- Please try again by typing the **%s" + "member** command.",
                    user.getAsMention(), Config.PREFIX))));
    }

    private static void checkResponse(Event event, MessageReceivedEvent e, EventWaiter waiter) {
        User user = e.getAuthor();
        String response = e.getMessage().getContentRaw().toLowerCase();
        Guild guild = event.getJDA().getGuilds().stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        if (response.toLowerCase().contains("oboro")) {
            Role memberRole = guild.getRolesByName("Member", false).stream()
                .findFirst()
                .orElseThrow(IllegalStateException::new);
            Member member = FinderUtil.findMembers(user.getId(), guild)
                .stream()
                .findFirst()
                .orElseThrow(IllegalStateException::new);
            new GuildController(guild).addRolesToMember(member, memberRole)
                .queue();
            MessageUtil.sendMessage(user, EmojiUtil.getCustomEmoji(e.getJDA(), "sakura"));
            MessageUtil.sendMessage(user, "- Correct.");
        } else if (response.contains("edwin black")) {
            MessageUtil.sendMessage(user,
                String.format("- Almost! But not what I was looking for, try again %s",
                    EmojiUtil.getCustomEmoji(e.getJDA(), "sakurayum")));
            waitForResponse(user, event, waiter);
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
