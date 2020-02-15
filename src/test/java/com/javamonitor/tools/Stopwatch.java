package com.javamonitor.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * A simple stopwatch timer to trace slow method calls.
 * This is a faster and leaner alternative to {@link org.springframework.util.StopWatch},
 * please note the differences in usage and output.
 *
 * @author Kees Jan Koster &lt;kjkoster@kjkoster.org&gt;
 */
public final class Stopwatch implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(Stopwatch.class);

    private final long start;
    private long lastTime;
    private Level loglevel = Level.WARN;

    private final StringBuilder message = new StringBuilder();

    /**
     * Start a new stopwatch, specifying the class we work for.
     *
     * @param clazz The class we work for.
     */
    public Stopwatch(final Class<?> clazz) {
        this(clazz.getName());
    }

    /**
     * Start a new stopwatch, with a custom name.
     *
     * @param name The name of this Stopwatch
     */
    public Stopwatch(final String name) {
        super();

        start = System.currentTimeMillis();
        lastTime = start;

        message.append("entering ").append(name).append(" took ");
    }

    /**
     * Mark the time of the operation that we are about to perform.
     *
     * @param operation The operation we are about to perform.
     */
    public void aboutTo(final String operation) {
        final long now = System.currentTimeMillis();
        final long timeDiff = now - lastTime;
        lastTime = now;

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
        final long now = System.currentTimeMillis();
        final long timeDiff = now - lastTime;
        lastTime = now;

        long total = now - start;
        message.append(timeDiff).append(". Total: ").append(total).append(" ms.");

        if ((total) > thresholdMillis) {
            log.log(loglevel, message);
            return false;
        }
        return true;
    }

    public String getMessage(){
        return message.toString();
    }

    public Stopwatch setLoglevel(Level loglevel) {
        this.loglevel = loglevel;
        return this;
    }
}

