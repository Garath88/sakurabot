package com.sakura.bot.commands.thread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sakura.bot.database.ThreadDbTable;
import com.sakura.bot.utils.CategoryUtil;
import com.sakura.bot.utils.GuildUtil;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public final class SortThreads {

    private static final Logger LOGGER = LoggerFactory.getLogger(SortThreads.class);
    private static AtomicInteger threadCounter = new AtomicInteger();

    private SortThreads() {
    }

    public static void countUniquePostsAndSort(TextChannel thread, int amountOfThreads) {
        long threadId = thread.getIdLong();
        final long messageId;
        Long temp = ThreadDbTable.getLatestMsgId(threadId);
        if (temp == 0 && thread.hasLatestMessage()) {
            temp = thread.getLatestMessageIdLong();
            ThreadDbTable.storeLatestMsgId(temp, threadId);
        }
        if (temp > 0) {
            messageId = temp;
            try {
                MessageHistory history = thread.getHistoryAfter(messageId, 100)
                    .complete();
                Message latestMsg = thread.getMessageById(messageId)
                    .complete();
                sortAndDoInactivityTask(history, thread, latestMsg, amountOfThreads);
            } catch (Exception e) {
                LOGGER.error("Failed to sort threads", e);
            }
        }
    }

    private static void sortAndDoInactivityTask(MessageHistory history, TextChannel thread, Message latestMessage, int amountOfThreads) {
        boolean isLastThread = checkIfLastThread(amountOfThreads);
        countAndStorePostCount(history, latestMessage, thread);
        if (isLastThread) {
            JDA jda = thread.getJDA();
            List<TextChannel> allThreads = CategoryUtil.getThreadCategory(jda)
                .getTextChannels();
            sortAllThreadsByPostCountAndStartOrCancelInactivityTask(thread.getJDA(), allThreads);
        }
    }

    private static boolean checkIfLastThread(int amountOfThreads) {
        if (threadCounter.incrementAndGet() == amountOfThreads) {
            threadCounter.set(0);
            return true;
        } else {
            return false;
        }
    }

    private static void sortAllThreadsByPostCountAndStartOrCancelInactivityTask(JDA jda, List<TextChannel> allThreads) {
        Category category = CategoryUtil.getThreadCategory(jda);
        if (!allThreads.isEmpty()) {
            GuildUtil.getGuild(jda).getController()
                .modifyTextChannelPositions(category)
                .sortOrder(Comparator.comparingInt(ThreadDbTable::getPostCount)
                    .reversed())
                .queue(success ->
                    InactiveThreadChecker.startOrCancelInactivityTaskIfNotTopX(jda));
        }
    }

    private static void countAndStorePostCount(MessageHistory msgHistory, Message latestMsg, TextChannel thread) {
        List<Message> messages = new ArrayList<>(msgHistory.getRetrievedHistory());
        messages.add(latestMsg);
        long threadId = thread.getIdLong();
        Integer postCount = ThreadDbTable.getPostCount(thread);
        postCount += countUniqueNewMessages(messages, threadId);
        ThreadDbTable.storePostCount(postCount, threadId);
    }

    private static int countUniqueNewMessages(List<Message> messages, long threadId) {
        ListIterator<Message> iter = messages.listIterator();
        int postCount = 0;
        if (messages.size() > 1) {
            ThreadDbTable.storeLatestMsgId(
                messages.get(0).getIdLong(), threadId);
            postCount = countUniqueMessages(iter);
        }
        return postCount;
    }

    private static int countUniqueMessages(ListIterator<Message> iter) {
        int postCount = 0;
        while (iter.hasNext()) {
            Message latestMessage = iter.next();
            if (iter.hasNext()) {
                Message previousMessage = iter.next();
                User currentAuthor = latestMessage.getAuthor();
                User previousAuthor = previousMessage.getAuthor();
                if (!previousAuthor.isBot() && !currentAuthor.isBot() &&
                    (currentAuthor.getIdLong() != previousAuthor.getIdLong())
                    || !latestMessage.getAttachments().isEmpty()) {

                    postCount++;
                }
                iter.previous();
            }
        }
        return postCount;
    }
}
