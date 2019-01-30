package com.sakura.bot.utils;

import org.apache.commons.lang3.StringUtils;

public final class ArgumentChecker {
    private static final String SPLIT_BY_SPACE = "\\s+";
    private static final String SPIT_BY_PIPE = "\\|+";
    private static final String INVALID_ARGS_MSG = "Wrong number of arguments, expected: %d";

    private ArgumentChecker() {
    }

    public static void checkArgsBySpace(String arguments, int requiredArguments) {
        if (getNumberOfArguments(arguments, SPLIT_BY_SPACE) != requiredArguments) {
            throw new IllegalArgumentException(String.format(INVALID_ARGS_MSG,
                requiredArguments));
        }
    }

    public static void checkArgsByPipe(String arguments, int requiredArguments) {
        if (getNumberOfArguments(arguments, SPIT_BY_PIPE) != requiredArguments) {
            throw new IllegalArgumentException(String.format(INVALID_ARGS_MSG,
                requiredArguments));
        }
    }

    private static int getNumberOfArguments(String arguments, String regex) {
        String[] items = arguments.split(regex);
        if (StringUtils.isEmpty(arguments)) {
            return 0;
        }
        return items.length;
    }

    public static void checkIfArgsAreNotEmpty(String arguments) {
        if (StringUtils.isEmpty(arguments)) {
            throw new IllegalArgumentException("You didn't give me any arguments!");
        }
    }
}


