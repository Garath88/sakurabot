package com.sakura.bot.utils;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.impl.PrivateChannelImpl;
import net.dv8tion.jda.core.entities.impl.ReceivedMessage;

public final class PrivateChannelWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateChannelWrapper.class);

    private PrivateChannelWrapper() {
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T, E extends Exception> {
        void accept(T t) throws E;
    }

    public static <T> Consumer<T> userIsInGuild(
        ThrowingConsumer<T, Exception> throwingConsumer) {

        return ret -> {
            try {
                if (ret instanceof ReceivedMessage) {
                    ReceivedMessage msg = (ReceivedMessage)ret;
                    MessageChannel chan = msg.getChannel();
                    if (chan instanceof PrivateChannelImpl) {
                        PrivateChannelImpl pc = (PrivateChannelImpl)chan;
                        if (GuildUtil.userIsInGuild(pc.getUser())) {
                            throwingConsumer.accept(ret);
                        }
                    }
                } else {
                    throwingConsumer.accept(ret);
                }
            } catch (Exception ex) {
                LOGGER.warn("Quiz error: {}", ex.getMessage());
            }
        };
    }
}
