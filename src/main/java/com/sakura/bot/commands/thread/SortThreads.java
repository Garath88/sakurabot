package com.sakura.bot.commands.thread;

import java.util.Comparator;
import java.util.stream.Collectors;

import com.sakura.bot.utils.CategoryUtil;
import com.sakura.bot.utils.GuildUtil;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.TextChannel;

public final class SortThreads {

    private SortThreads() {
    }

    public static void sortByPostCount(JDA jda) {
        Category category = CategoryUtil.getThreadCategory(jda);
        if (!category.getTextChannels().isEmpty()) {
            GuildUtil.getGuild(jda).getController()
                .modifyTextChannelPositions(category)
                .sortOrder(Comparator.comparingInt(SortThreads::getPostCount)
                    .reversed())
                .queue();
        }
    }

    private static int getPostCount(TextChannel thread) {
        int postCount;
        postCount = thread.getIterableHistory().stream()
            .collect(Collectors.toList()).size();
        return postCount;
    }
}
