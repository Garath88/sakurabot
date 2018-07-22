package com.sakura.bot.commands.topic;

import java.time.OffsetDateTime;

import com.sakura.bot.tasks.Task;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public final class InactiveChannelCheckTask extends Task {
    private final TextChannel customTextChannel;
    private static final int EXPIRE_TIME = 10;
    private boolean inactive = false;

    InactiveChannelCheckTask(TextChannel customTextChannel) {
        super(1, 1);
        this.customTextChannel = customTextChannel;
    }

    @Override
    public void execute() {
        if (customTextChannel.hasLatestMessage()) {
            String latestMessageId = customTextChannel.getLatestMessageId();
            Message latestMessage = customTextChannel.getMessageById(latestMessageId)
                .complete();
            checkIfChannelIsInactive(latestMessage.getCreationTime());
        }
    }

    private void checkIfChannelIsInactive(OffsetDateTime creationTime) {
        OffsetDateTime creationDatePlusExpireTime = creationTime.plusMinutes(EXPIRE_TIME);
        System.out.println(OffsetDateTime.now().isAfter(creationDatePlusExpireTime));
    }
}
