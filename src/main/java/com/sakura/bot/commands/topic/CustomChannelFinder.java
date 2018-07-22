package com.sakura.bot.commands.topic;

import java.util.Collections;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.TextChannel;

public final class CustomChannelFinder {

    private CustomChannelFinder() {
    }

    public static List<TextChannel> findCustomTextChannels(CommandEvent event) {
        // TODO remove hardcoded CUSTOM category
        List<Category> customCategories = FinderUtil.findCategories("CUSTOM", event.getGuild());
        if (!customCategories.isEmpty()) {
            return customCategories.get(0).getTextChannels();
        }
        return Collections.emptyList();
    }
}
