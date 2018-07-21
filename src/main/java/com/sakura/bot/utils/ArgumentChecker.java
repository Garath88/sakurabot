package com.sakura.bot.utils;

public final class ArgumentChecker {
    private ArgumentChecker() {
    }

    public static void checkArgsBySpace(String arguments, int requiredArguments) {
        String[] items = arguments.split("\\s+");
        if (items.length != requiredArguments) {
            throw new IllegalArgumentException("Wrong number of arguments, `" + items[0] + "`");
        }
    }

    public static void checkIfArgsAreNotEmpty(String arguments) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("You didn't give me any arguments!");
        }
    }
}


