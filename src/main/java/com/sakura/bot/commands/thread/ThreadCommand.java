package com.sakura.bot.commands.thread;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.configuration.Config;
import com.sakura.bot.database.ThreadDbTable;
import com.sakura.bot.database.ThreadInfo;
import com.sakura.bot.quiz.QuizQuestion;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.CategoryUtil;
import com.sakura.bot.utils.RoleUtil;
import com.sakura.bot.utils.TextChannelUtil;
import com.sakura.bot.utils.WordBlacklist;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

public class ThreadCommand extends Command {
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[^\\w ]");
    private static final int MAX_AMOUNT_OF_THREADS = 12;
    private static final int LURKER_MAX_THREAD_LIMIT = 1;

    public ThreadCommand() {
        this.name = "thread";
        this.help = "creates a new thread with topic.";
        this.arguments = "<topic>";
        this.botPermissions = new Permission[] {
            Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        if (CategoryUtil.getThreadCategory(event.getJDA())
            .getTextChannels().size() >= MAX_AMOUNT_OF_THREADS) {
            sendErrorMsg("Sorry maximum amount of threads reached!", event);
        } else if (isAtMaxThreadsForUser(event)) {
            sendErrorMsg(String.format(
                "Sorry you can only make %s thread for your current role.",
                LURKER_MAX_THREAD_LIMIT), event);
        } else {
            addNewThread(event);
        }
    }

    private void sendErrorMsg(String msg, CommandEvent event) {
        event.reply(msg);
        event.reactError();
    }

    private boolean isAtMaxThreadsForUser(CommandEvent event) {
        if (RoleUtil.getMemberRoles(event).isEmpty()) {
            ThreadInfo threadInfo = ThreadDbTable.getThreadInfoFromUser(event.getAuthor());
            return threadInfo.getThreadIds().size() >= LURKER_MAX_THREAD_LIMIT;
        }
        return false;
    }

    private void addNewThread(CommandEvent event) {
        String topic = event.getArgs();
        try {
            createNewThread(event, topic, true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            event.replyWarning(String.format("%s %s",
                event.getMessage().getAuthor().getAsMention(), e.getMessage()));
        }
    }

    public static void createNewThread(CommandEvent event, String topic, boolean storeInDatabase) {
        ArgumentChecker.checkIfArgsAreNotEmpty(topic);
        validateTopicName(topic, event);
        createThreadChannel(event, topic, storeInDatabase);
    }

    private static void validateTopicName(String topic, CommandEvent event) {
        if (StringUtils.isNotEmpty(topic) && topic.length() >= 2 && topic.length() <= 100) {
            topic = topic.replaceAll("'", "");
            Matcher matcher = SYMBOL_PATTERN.matcher(topic);
            if (matcher.find()) {
                throw new IllegalArgumentException("Invalid input, topic can not contain special character");
            }
            String badWord = WordBlacklist.searchBadWord(topic);
            if (StringUtils.isNotEmpty(badWord)) {
                User owner = event.getJDA().getUserById(Config.getOwnerId());
                event.reply(owner.getAsMention() + " says: \nhttps://i.makeagif.com/media/2-21-2015/RDVwim.gif");
                throw new IllegalArgumentException(String.format("Found blacklisted phrase **%s** in topic name", badWord));
            }
        } else {
            throw new IllegalArgumentException("Topic can not be empty and must be between 2-100 characters");
        }
    }

    private static void createThreadChannel(CommandEvent event, String topic, boolean storeInDatabase) {
        net.dv8tion.jda.core.entities.Category threadCategory = CategoryUtil.getThreadCategory(event.getJDA());
        validateThreadName(threadCategory, topic);
        final String channelTopic = topic.replaceAll(" ", "` `");
        event.getGuild().getController().createTextChannel(channelTopic)
            .setTopic(topic)
            .setNSFW(true)
            .setParent(threadCategory)
            .queue(chan -> doTasks(chan, event, topic, storeInDatabase));
        event.reply(String.format("Succesfully created new thread: **%s**", topic));
    }

    private static void validateThreadName(net.dv8tion.jda.core.entities.Category customCategory, String topic) {
        if (customCategory.getTextChannels().stream().anyMatch(chan -> chan.getName().equals(topic))) {
            throw new IllegalArgumentException(String.format(
                "This thread already exists! **%s**", topic));
        }
    }

    private static void doTasks(Channel threadChannel, CommandEvent event, String topic, boolean storeInDatabase) {
        threadChannel.createPermissionOverride(event.getGuild()
            .getPublicRole())
            .setDeny(Permission.CREATE_INSTANT_INVITE)
            .queue();
        setDenyForRole(threadChannel, event, QuizQuestion.QUIZ_ROLE);
        setDenyForRole(threadChannel, event, QuizQuestion.RULES_ROLE);

        TextChannel threadTextChannel = findThreadTextChannel(threadChannel, event.getEvent());
        if (storeInDatabase) {
            ThreadDbTable.addThread(event.getMember()
                .getUser(), threadChannel);
            sendTopicHasBeenSetMsg(threadTextChannel, topic);
            InactiveThreadChecker.startOrCancelInactivityTaskIfNotTopX(threadTextChannel);
        } else { //Sakura thread
            ThreadDbTable.addThread(event.getSelfUser(), threadChannel);
            ThreadDbTable.storePostCount(9999,
                threadChannel.getIdLong());
        }
    }

    private static void setDenyForRole(Channel threadChannel, CommandEvent event, String roleName) {
        threadChannel.createPermissionOverride(RoleUtil.findRole(event.getGuild(), roleName))
            .setDeny(Permission.MESSAGE_READ)
            .queue();
    }

    /*TODO: is event needed? perhaps use JDA?*/
    private static TextChannel findThreadTextChannel(Channel threadChannel, Event event) {
        return TextChannelUtil.getChannel(threadChannel.getId(), event);
    }

    private static void sendTopicHasBeenSetMsg(TextChannel threadTextChannel, String topic) {
        threadTextChannel.sendMessage("The topic has now been set to: " +
            String.format("**%s**", topic))
            .queue(msg -> {
                long threadId = threadTextChannel.getIdLong();
                ThreadDbTable.storeLatestMsgId(
                    msg.getIdLong(), threadId);
                ThreadDbTable.storePostCount(0, threadId);
            });
    }
}