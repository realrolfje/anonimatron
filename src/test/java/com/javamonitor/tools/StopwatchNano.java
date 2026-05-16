package com.javamonitor.tools;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

/**
 * A simple stopwatch timer to trace slow method calls.
 * This is a faster and leaner alternative to {@link org.springframework.util.StopWatch},
 * please note the differences in usage and output.
 *
 * @author Kees Jan Koster &lt;kjkoster@kjkoster.org&gt;
 */
public final class StopwatchNano implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = LogManager.getLogger(StopwatchNano.class);

    private final long startNanos;
    private long lastTimeNanos;
    private Level loglevel = Level.WARN;

    private final StringBuilder message = new StringBuilder();

    /**
     * Start a new stopwatch, specifying the class we work for.
     *
     * @param clazz The class we work for.
     */
    public StopwatchNano(final Class<?> clazz) {
        this(clazz.getName());
    }

    /**
     * Start a new stopwatch, with a custom name.
     *
     * @param name The name of this Stopwatch
     */
    public StopwatchNano(final String name) {
        super();

        startNanos = System.nanoTime();
        lastTimeNanos = startNanos;

        message.append("entering ").append(name).append(" took ");
    }

    /**
     * Mark the time of the operation that we are about to perform.
     *
     * @param operation The operation we are about to perform.
     */
    public void aboutTo(final String operation) {
        final long now = System.nanoTime();
        final long timeDiff = now - lastTimeNanos;
        lastTimeNanos = now;

        message.append(timeDiff).append("; ").append(operation)
                .append(" took ");
    }

    /**
     * Stop the stopwatch, logging the events in case the time was longer than
     * the specified threshold time value. This method is typically invoked in a
     * finally block.
     *
     * @param thresholdMillis The threshold above which we print the events.
     * @return <code>true</code> if the operation completes within the specified time
     */
    public boolean stop(final long thresholdMillis) {
        final long now = System.nanoTime();
        final long timeDiff = now - lastTimeNanos;
        lastTimeNanos = now;

        long total = now - startNanos;
        message.append(timeDiff).append(". Total: ").append(total).append(" ns.");

        if ((total) > (thresholdMillis * 1_000_000)) {
            log.log(loglevel, message);
            return false;
        }
        return true;
    }

    public String getMessage() {
        return message.toString();
    }

    public StopwatchNano setLoglevel(Level loglevel) {
        this.loglevel = loglevel;
        return this;
    }
}

