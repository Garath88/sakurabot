package com.sakura.bot.utils;

import java.util.Arrays;

public final class ArgumentChecker {
    private ArgumentChecker() {
    }

    public static void checkArgsBySpace(String arguments, int requiredArguments) {
        String[] items = arguments.split("\\s+");
        if (requiredArguments == 0 && !Arrays.toString(items).equals("[]")
            && items.length != requiredArguments) {
            throw new IllegalArgumentException(String.format("Wrong number of arguments, expected: %d",
                requiredArguments));
        }
    }

    public static void checkIfArgsAreNotEmpty(String arguments) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("You didn't give me any arguments!");
        }
    }
}


