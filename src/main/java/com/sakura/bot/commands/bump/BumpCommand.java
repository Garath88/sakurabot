package com.sakura.bot.commands.bump;

import java.util.List;
import java.util.TimerTask;

import com.sakura.bot.Roles;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.configuration.Config;

public final class BumpCommand extends Command {
    private static boolean running;

    public BumpCommand() {
        this.name = "bump";
        this.help = "repeat commands periodically";
        this.requiredRoles = Roles.MODERATOR.getValues();
    }

    static boolean isRunning() {
        return running;
    }

    @Override
    protected void execute(CommandEvent event) {
        List<BumpTask> bumpTasks = BumpTaskListContainer.getBumpTaskList();
        if (bumpTasks.isEmpty()) {
            event.replyWarning(
                "You haven't added any commands to bump! \n "
                    + "Please use the **" + Config.PREFIX + "bump_add** command");
        } else {
            List<BumpTask> queuedTasks = BumpTaskListContainer.getQueuedTasks();
            if (queuedTasks.isEmpty()) {
                stopBump(event, bumpTasks);
            } else {
                startBump(event, queuedTasks);
            }
        }
    }

    static void startBump(CommandEvent event, List<BumpTask> queuedTasks) {
        if (!running) {
            event.reply("**Starting bumping..** \n"
                + "_I'll make ya bump hump wiggle and shake your rump!_");
        }
        running = true;
        queuedTasks.forEach(BumpTask::scheduleBumpTask);
    }

    private static void stopBump(CommandEvent event, List<BumpTask> bumpTasks) {
        event.reply("**Stopping bumping..** \n"
            + "_I got you jumpin' an' bumpin' an' pumpin' movin' all around, G_");
        running = false;
        bumpTasks.forEach(TimerTask::cancel);
        bumpTasks.clear();
    }
}
