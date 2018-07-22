package com.sakura.bot;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;

interface StartupListener extends EventListener {
    void runStartupCommands(Event event);

    @Override
    default void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            runStartupCommands(event);
        }
    }
}
