package com.sakura.bot.commands.thread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import com.sakura.bot.database.ThreadDbTable;
import com.sakura.bot.utils.CategoryUtil;
import com.sakura.bot.utils.GuildUtil;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

public final class SortThreads {

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
            MessageHistory history = thread.getHistoryAfter(messageId, 100)
                .complete();
            if (history.size() > 0) {
                Message latestMsg = thread.getMessageById(messageId)
                    .complete();
                sortAndDoInactivityTask(history, thread, latestMsg, amountOfThreads);
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
            long currentAuthor = latestMessage.getAuthor()
                .getIdLong();
            if (iter.hasNext()) {
                Message previousMessage = iter.next();
                if (currentAuthor != previousMessage.getAuthor().getIdLong()
                    || !latestMessage.getAttachments().isEmpty()) {

                    postCount++;
                }
                iter.previous();
            }
        }
        return postCount;
    }
}