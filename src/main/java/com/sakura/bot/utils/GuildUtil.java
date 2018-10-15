package com.sakura.bot.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public final class GuildUtil {
    private GuildUtil() {
    }

    public static Guild getGuild(JDA jda) {
        return jda.getGuilds().stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    static boolean userIsInGuild(User user) {
        Member member = GuildUtil.getGuild(user.getJDA()).getMemberById(user.getId());
        return member != null;
    }
}
