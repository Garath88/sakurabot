package com.sakura.bot.commands.say;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sakura.bot.Permissions;
import com.sakura.bot.commands.thread.ThreadCommand;
import com.sakura.bot.commands.thread.ThreadInfo;
import com.sakura.bot.configuration.Config;
import com.sakura.bot.utils.EmojiUtil;
import com.sakura.bot.utils.MentionUtil;
import com.sakura.bot.utils.MessageUtil;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;

public final class SakuraSayCommand extends Command {
    private static final int MESSAGE_INDEX = 0;
    private static final int THREAD_INDEX = 1;
    private static final int MESSAGE_WITH_CHANNEL_ID = 2;

    public SakuraSayCommand() {
        this.name = "sakura_say";
        this.help = "say something with Sakura and optionally create a channel"
            + " or with no arguments to list current talking channel.";
        this.arguments = "[<text>] followed by separator '|' [<topic>]";
        this.guildOnly = true;
        this.requiredRoles = Permissions.MODERATOR.getValues();
        this.botPermissions = new Permission[] {
            Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String[] message = event.getArgs().split("\\|");
            if (message.length == 1 && message[0].equals("-")) {
                SakuraSayStorage.toggleUseDash(event);
            } else {
                say(event, message[MESSAGE_INDEX]);
                if (message.length == MESSAGE_WITH_CHANNEL_ID) {
                    String name = message[THREAD_INDEX].trim();
                    ThreadCommand.createNewThread(event,
                        new ThreadInfo(name, name, false));
                }
            }
        } catch (IllegalArgumentException e) {
            event.replyWarning(e.getMessage());
        }
    }

    private void say(CommandEvent event, String message) {
        TextChannel textChannel = SakuraSayStorage.getChannel()
            .orElseThrow(() -> new IllegalArgumentException("You haven't added a text channel to talk in! \n "
                + "Please use the **" + Config.PREFIX + "sakura_set_chan** command"));
        if (textChannelExists(textChannel, event.getJDA().getTextChannels())) {
            List<Message.Attachment> attachments = event.getMessage().getAttachments();
            if (StringUtils.isNotEmpty(message) || !attachments.isEmpty()) {
                if (event.isFromType(ChannelType.PRIVATE)) {
                    message = MentionUtil.addMentionsToMessage(event, message);
                    message = EmojiUtil.addEmojisToMessage(event.getJDA(), message);
                }
                MessageUtil.sendAttachmentsToChannel(attachments, textChannel);
                MessageUtil.sendMessageToChannel(message, textChannel, SakuraSayStorage.getUseDash());
            } else {
                event.reply(String.format("Currently talking in channel: **%s**",
                    textChannel.getName()));
            }
        }
    }

    private boolean textChannelExists(MessageChannel textChannel, List<TextChannel> textChannels) {
        return textChannels.stream()
            .anyMatch(chan -> chan.getId().equals(textChannel.getId()));
    }
}
