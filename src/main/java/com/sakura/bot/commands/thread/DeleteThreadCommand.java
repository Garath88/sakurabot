package com.sakura.bot.commands.thread;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sakura.bot.configuration.Config;
import com.sakura.bot.database.ThreadDbTable;
import com.sakura.bot.database.ThreadInfo;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.CategoryUtil;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DeleteThreadCommand extends Command {
    private static final int RESPONSE_TIMEOUT_IN_SEC = 35;
    private final EventWaiter waiter;

    public DeleteThreadCommand(EventWaiter waiter) {
        this.waiter = waiter;
        this.name = "deletethread";
        this.help = "choose a created thread to delete.";
        this.botPermissions = new Permission[] {
            Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            ArgumentChecker.checkArgsBySpace(event.getArgs(), 0);
            ThreadInfo customChannelInfo = ThreadDbTable.getThreadInfo(event.getMember().getUser());
            event.reply(String.format("Listing created threads for %s: %n",
                event.getMessage().getAuthor().getAsMention()));
            event.reply(customChannelInfo.getlistedChannels());
            if (!customChannelInfo.getThreadIds().isEmpty()) {
                event.reply("Please type in the number of the thread you want to delete.");
                // wait for a response
                waiter.waitForEvent(MessageReceivedEvent.class,
                    // make sure it's by the same user, and in the same channel
                    e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                    // respond, inserting the name they listed into the response
                    e -> deleteChannel(event, e, customChannelInfo),
                    // if the user takes more than a minute, time out
                    RESPONSE_TIMEOUT_IN_SEC, TimeUnit.SECONDS, () -> event.reply(String.format("Sorry %s, you took too long.",
                        event.getMessage().getAuthor().getAsMention())));
            }
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void deleteChannel(CommandEvent event, MessageReceivedEvent messageEvent,
        ThreadInfo customChannelInfo) {

        String message = messageEvent.getMessage().getContentRaw();
        try {
            validateInput(message, customChannelInfo.getThreadIds().size());
            Optional<TextChannel> threadToBeDeleted = getThreadChannel(event, customChannelInfo.getThreadIds()
                .get(Integer.valueOf(message) - 1));
            if (threadToBeDeleted.isPresent()) {
                TextChannel thread = threadToBeDeleted.get();
                thread.delete()
                    .queue();
                event.reply(String.format("Successfully deleted thread: **%s**",
                    thread.getName()));
            } else {
                event.replyError("Could not delete channel!");
            }
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage() + "\n" +
                String.format("%s Please try running the %s command again",
                    event.getMessage().getAuthor().getAsMention(),
                    Config.PREFIX + name));
        }

    }

    private void validateInput(String args, int numberOfchannels) {
        ArgumentChecker.checkArgsBySpace(args, 1);
        Preconditions.checkArgument(StringUtils.isNumeric(args),
            String.format("Invalid thread id \"%s\", id must be numeric", args));
        Preconditions.checkArgument(Double.parseDouble(args) != 0,
            "ID must be greater than zero!");
        Preconditions.checkArgument(Integer.valueOf(args) <= numberOfchannels,
            String.format("No thread found with ID #%s", args));
    }

    private Optional<TextChannel> getThreadChannel(CommandEvent event, Long id) {
        return CategoryUtil.getThreadCategory(event.getJDA())
            .getTextChannels().stream()
            .filter(chan -> chan.getIdLong() == id)
            .findFirst();
    }
}
