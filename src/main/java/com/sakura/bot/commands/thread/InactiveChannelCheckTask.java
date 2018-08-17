package com.sakura.bot.commands.thread;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.sakura.bot.configuration.Config;
import com.sakura.bot.tasks.Task;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public final class InactiveChannelCheckTask extends Task {
    private static final int EXPIRE_TIME_IN_MINUTES = 60;
    private static final int WARNING_TIME_IN_MINUTES = 30;
    private final TextChannel customTextChannel;

    InactiveChannelCheckTask(TextChannel customTextChannel) {
        super(WARNING_TIME_IN_MINUTES, 0);
        this.customTextChannel = customTextChannel;
    }

    @Override
    public void execute() {
        if (customTextChannel.hasLatestMessage()) {
            checkIfChannelIsInactive();
        }
    }

    private void checkIfChannelIsInactive() {
        List<Message> latestMessages = getLatestMessages(5);
        if (shouldWarn(latestMessages)) {
            customTextChannel.sendMessage(String.format("This channel has been marked as **inactive** "
                + "and will be deleted in %d minutes due to inactivity!", WARNING_TIME_IN_MINUTES))
                .queue();
        } else if (shouldDelete(latestMessages)) {
            deleteChannel();
        }
    }

    private List<Message> getLatestMessages(int limit) {
        return customTextChannel.getIterableHistory().stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    private boolean shouldWarn(List<Message> messages) {
        Message latestMessage = messages.get(0);
        return !latestMessage.getAuthor().getName().equals(Config.BOT_NAME) &&
            OffsetDateTime.now().isAfter(getWarningTime(latestMessage));
    }

    private OffsetDateTime getWarningTime(Message latestMessage) {
        return latestMessage.getCreationTime()
            .plusMinutes((long)EXPIRE_TIME_IN_MINUTES - WARNING_TIME_IN_MINUTES);
    }

    private boolean shouldDelete(List<Message> latestMessages) {
        Message latestNonBotMsg = getLatestNonBotMessage(latestMessages);
        if (latestNonBotMsg != null) {
            return isInactive(latestNonBotMsg);
        } else {
            Message latestBotMsg = latestMessages.get(0);
            return isInactive(latestBotMsg);
        }
    }

    private boolean isInactive(Message message) {
        return OffsetDateTime.now().isAfter(getWarningTime(message)
            .plusMinutes(WARNING_TIME_IN_MINUTES));
    }

    private Message getLatestNonBotMessage(List<Message> messages) {
        List<Message> latestNonBotMsgs = messages.stream()
            .filter(msg -> !msg.getAuthor().isBot())
            .collect(Collectors.toList());
        if (!latestNonBotMsgs.isEmpty()) {
            return latestNonBotMsgs.get(0);
        }
        return null;
    }

    private void deleteChannel() {
        InactiveChannelTaskList.getTaskListContainer()
            .cancelTask(this);
        customTextChannel.delete()
            .queue();
    }
}