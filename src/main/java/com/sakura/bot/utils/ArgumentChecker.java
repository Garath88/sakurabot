package com.sakura.bot.utils;

import org.apache.commons.lang3.StringUtils;

public final class ArgumentChecker {
    private ArgumentChecker() {
    }

    public static void checkArgsBySpace(String arguments, int requiredArguments) {
        if (getNumberOfArguments(arguments) != requiredArguments) {
            throw new IllegalArgumentException(String.format("Wrong number of arguments, expected: %d",
                requiredArguments));
        }
    }

    private static int getNumberOfArguments(String arguments) {
        String[] items = arguments.split("\\s+");
        if (StringUtils.isEmpty(arguments)) {
            return 0;
        }
        return items.length;
    }

    public static void checkIfArgsAreNotEmpty(String arguments) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("You didn't give me any arguments!");
        }
    }
}


