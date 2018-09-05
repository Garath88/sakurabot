package com.sakura.bot.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WordBlacklist {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordBlacklist.class);
    private static final Set<String> BAD_WORDS = loadBadWords();

    private WordBlacklist() {
    }

    private static Set<String> loadBadWords() {
        Set<String> list = new HashSet<>();
        try {
            List<String> temp = TxtReader.readTxtFile("/blacklist.txt");
            for (String item : temp) {
                if (!list.add(item)) {
                    String warn = String.format("duplicated blacklist word \"%s\" found!",
                        item);
                    LOGGER.warn(warn);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load blacklist file");
        }
        return list;
    }

    static boolean containsBadWord(String input) {
        if (StringUtils.isNotEmpty(searchBadWord(input))) {
            return true;
        }
        String debug = String.format("%s not found in blacklist", input);
        LOGGER.debug(debug);
        return false;
    }

    public static String searchBadWord(String input) {
        String haystack = input.toLowerCase()
            .replaceAll("\\s+", "");
        String badWord = "";
        for (String needle : BAD_WORDS) {
            if (needle.contains("|")) {
                String delimtedBadWord = needle.replaceAll("\\|", "");
                String pattern = "\\b" + delimtedBadWord + "\\b";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(input);
                if (m.find()) {
                    badWord = delimtedBadWord;
                    String debug = String.format("%s contains %s", input, delimtedBadWord);
                    LOGGER.debug(debug);
                }
            } else {
                if (haystack.contains(needle)) {
                    badWord = needle;
                    String debug = String.format("%s contains %s", input, needle);
                    LOGGER.debug(debug);
                }
            }
        }
        return badWord;
    }
}
