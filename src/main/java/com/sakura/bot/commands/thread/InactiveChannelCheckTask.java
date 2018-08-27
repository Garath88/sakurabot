package com.sakura.bot.commands.thread;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import com.sakura.bot.tasks.Task;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public final class InactiveChannelCheckTask extends Task {
    private static final int EXPIRE_ATER_MINUTES = 24 * 60;
    private static final int WARNING_BEFORE_MINUTES = 8 * 60;
    private static final int FINAL_WARNING_BEFORE_MINUTES = 60;
    private final TextChannel customTextChannel;

    InactiveChannelCheckTask(TextChannel customTextChannel) {
        super(WARNING_BEFORE_MINUTES / 2, 0);
        this.customTextChannel = customTextChannel;
    }

    private InactiveChannelCheckTask(TextChannel customTextChannel,
        long loopTime, long delay) {
        super(loopTime, delay);
        this.customTextChannel = customTextChannel;
    }

    @Override
    public void execute() {
        if (customTextChannel.getJDA().getTextChannels().stream()
            .anyMatch(chan -> chan.getId().equals(customTextChannel.getId())
                && chan.hasLatestMessage())) {
            checkIfChannelIsInactive();
        }
    }

    private void checkIfChannelIsInactive() {
        long timeLeft = getExpirationTimeInMinutes();
        if (timeLeft <= 0) {
            deleteChannel();
        } else if (timeLeft <= FINAL_WARNING_BEFORE_MINUTES) {
            customTextChannel.sendMessage(createInactivityMessage())
                .queue();
            scheduleDelete(timeLeft, 0);
        } else if (timeLeft <= WARNING_BEFORE_MINUTES) {
            customTextChannel.sendMessage(createInactivityMessage())
                .queue();
            scheduleDelete(timeLeft, FINAL_WARNING_BEFORE_MINUTES);
        }
    }

    private void scheduleDelete(long timeLeft, long warningTime) {
        reScheduleTask(new InactiveChannelCheckTask(customTextChannel,
            1, timeLeft - warningTime));
    }

    private List<Message> getLatestMessages() {
        return customTextChannel.getIterableHistory().stream()
            .limit(5)
            .collect(Collectors.toList());
    }

    private long getExpirationTimeInMinutes() {
        List<Message> latestMessages = getLatestMessages();
        OffsetDateTime creationTime = getLatestMessageTime(latestMessages);
        OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.UTC);
        return currentTime.until(creationTime.plusMinutes(EXPIRE_ATER_MINUTES),
            ChronoUnit.MINUTES);
    }

    private OffsetDateTime getLatestMessageTime(List<Message> latestMessages) {
        Message latestUserMsg = getLatestUserMessage(latestMessages);
        if (latestUserMsg != null) {
            return latestUserMsg.getCreationTime();
        }
        return customTextChannel.getCreationTime();
    }

    private Message getLatestUserMessage(List<Message> messages) {
        List<Message> latestUserMsgs = messages.stream()
            .filter(msg -> !msg.getAuthor().isBot())
            .collect(Collectors.toList());
        if (!latestUserMsgs.isEmpty()) {
            return latestUserMsgs.get(0);
        }
        return null;
    }

    private void deleteChannel() {
        InactiveChannelTaskList.getTaskListContainer()
            .cancelTask(this);
        customTextChannel.delete()
            .queue();
    }

    private String createInactivityMessage() {
        long expirationTime = getExpirationTimeInMinutes();
        long hours = expirationTime / 60;
        long minutes = expirationTime % 60;
        return String.format("This channel has been marked as **inactive** "
            + "and will be deleted in **%dh:%02dm** if no activity occurs!", hours, minutes);
    }

    private void reScheduleTask(Task newTask) {
        InactiveChannelTaskList.getTaskListContainer()
            .reScheduleTask(this,
                newTask);
    }
}