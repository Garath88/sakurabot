package com.sakura.bot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Category;

public final class CategoryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryUtil.class);

    private CategoryUtil() {
    }

    public static Category getCustomCategory(JDA jda) {
        // TODO remove hardcoded CUSTOM category
        return FinderUtil.findCategories("CUSTOM", jda)
            .stream()
            .findFirst()
            .orElseThrow(() -> {
                String errorMsg = "Custom category was not found!";
                LOGGER.error(errorMsg);
                return new IllegalStateException(errorMsg);
            });
    }
}