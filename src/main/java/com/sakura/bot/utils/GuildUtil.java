package com.sakura.bot.utils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.Event;

public final class GuildUtil {
    private GuildUtil() {
    }

    public static Guild getGuild(Event event) {
        return event.getJDA().getGuilds().stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }
}
