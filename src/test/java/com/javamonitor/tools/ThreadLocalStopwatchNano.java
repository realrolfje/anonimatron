package com.javamonitor.tools;

import org.apache.log4j.Level;

/**
 * Provides a {@link ThreadLocal} implementation of {@link StopwatchNano} to facilitate cross-object
 * timing without passing Stopwatch as an argument.
 * <p/>
 * Example:
 * ThreadLocalStopwatchNano.init("work on thread 1");
 * ThreadLocalStopwatchNano.aboutTo("do other work");
 * ThreadLocalStopwatchNano.aboutTo("finish up");
 * ThreadLocalStopwatchNano.stop(1000);
 * <p/>
 * The example above logs a Warning when the work in the current thread took more than 1000 milliseconds, and will
 * seperate stopwatches between threads. Running the same code in parallel results in two warnings in the log.
 *
 */
public class ThreadLocalStopwatchNano {
	private static ThreadLocal<StopwatchNano> stopwatch;

	public static void init(String name) {
		stopwatch.set(new StopwatchNano(name));
	}

	public static void aboutTo(String operation) {
		stopwatch.get().aboutTo(operation);
	}

	public static  void stop(final long thresholdMillis) {
		stopwatch.get().stop(thresholdMillis);
	}

	public static void setLoglevel(Level loglevel) {
		stopwatch.get().setLoglevel(loglevel);
	}

}
