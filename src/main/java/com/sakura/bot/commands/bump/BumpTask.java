package com.sakura.bot.commands.bump;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jagrosh.jdautilities.command.CommandEvent;

public final class BumpTask extends TimerTask {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final String commandToBump;
    private final int loopTime;
    private final CommandEvent event;

    BumpTask(String commandToBump, int loopTime, CommandEvent event) {
        this.commandToBump = commandToBump;
        this.loopTime = loopTime;
        this.event = event;
    }

    void scheduleBumpTask() {
        Timer timer = new Timer(true);
        int minutes = loopTime * 60000;
        timer.scheduleAtFixedRate(this, 0, minutes);
    }

    boolean isNotRunning() {
        return !running.get();
    }

    @Override
    public void run() {
        running.set(true);
        event.reply(commandToBump);
    }
}
