package com.sakura.bot.commands.thread;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.configuration.Config;
import com.sakura.bot.database.ThreadDbTable;
import com.sakura.bot.utils.ArgumentChecker;
import com.sakura.bot.utils.CategoryUtil;
import com.sakura.bot.utils.WordBlacklist;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class ThreadCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadCommand.class);
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[^\\w ]");
    private static final int MAX_AMOUNT_OF_THREADS = 10;

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
            .getTextChannels().size() <= MAX_AMOUNT_OF_THREADS) {
            addNewThread(event);
        } else {
            event.reply("Sorry maximum amount of threads reached!");
            event.reactError();
        }
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
        event.getGuild().getController().createTextChannel(topic)
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
        if (storeInDatabase) {
            ThreadDbTable.addThread(event.getMember()
                .getUser(), threadChannel);
        }
        TextChannel threadTextChannel = sendTopicHasBeenSetMsg(threadChannel, topic);
        InactiveThreadTaskList.startInactivityTask(threadTextChannel);
    }

    private static TextChannel sendTopicHasBeenSetMsg(Channel threadChannel, String topic) {
        TextChannel threadTextChannel = threadChannel.getGuild().getTextChannels().stream()
            .filter(channel -> channel.getId().equals(threadChannel.getId()))
            .findFirst().orElseThrow(() -> {
                String errorMsg = String.format(
                    "Something went really wrong, could not store created %s thread", topic);
                LOGGER.error(errorMsg);
                return new IllegalStateException(errorMsg);
            });
        threadTextChannel.sendMessage("The topic has now been set to: " +
            String.format("**%s**", topic))
            .queue();
        return threadTextChannel;
    }
}