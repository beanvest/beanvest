package beanvest.lib.apprunner;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.function.Consumer;

public class ProducingLogAppender extends AppenderBase<ILoggingEvent> {
    private static Consumer<ILoggingEvent> consumer;

    public static void setConsumer(Consumer<ILoggingEvent> consumer) {
        ProducingLogAppender.consumer = consumer;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (consumer != null) {
            consumer.accept(event);
        }
    }

    public static void clear()
    {
        consumer = null;
    }
}